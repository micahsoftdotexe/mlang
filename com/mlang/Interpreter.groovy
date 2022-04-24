package com.mlang;

import java.util.ArrayList;
import java.util.List;
import com.mlang.TokenType;
import static com.mlang.TokenType.*; // [static-import]
// import static com.mlang.TokenType.*;
class Interpreter implements Expr.Visitor<Object>,
                             Stmt.Visitor<Void> {
  final Environment globals = new Environment();
  private Environment environment = globals;

  Interpreter() {
    globals.define("clock", new MlangCallable() {
      @Override
      public int arity() { return 0; }

      @Override
      public Object call(Interpreter interpreter,
                         List<Object> arguments) {
        return (double)System.currentTimeMillis() / 1000.0;
      }

      @Override
      public String toString() { return "<native fn>"; }
    }, "FUNCTION_TYPE");
    globals.define("in", new MlangCallable() {
      @Override
      public int arity() { return 0; }

      @Override
      public Object call(Interpreter interpreter,
                         List<Object> arguments) {
        // Scanner inputScanner = new Scanner(System.in);
        // String input = inputScanner.nextLine();
        String input  = new java.util.Scanner(System.in).nextLine();
        return input;
      }

      @Override
      public String toString() { return "<native fn>"; }
    }, "FUNCTION_TYPE");
  }

  void interpret(List<Stmt> statements) {
    try {
      try {
        for (Stmt statement : statements) {
          execute(statement);
        }
      } catch (MlangRuntimeError error) {
        error.report();
        return;
      }
    } catch (MlangTypeError error) {
      error.report();
      return;
    }
    
  }

  public static typeCheck(Object value, String type) {
    if (value == null) {
      return true;
    }
    return DataType.DATA_TYPE_CHECKS.get(type)(value);
  }
  public static typeLookup(Object value) {
    for (String type : DataType.DATA_TYPE_CHECKS.keySet()) {
      //println("Type: " + type);
      if (DataType.DATA_TYPE_CHECKS.get(type)(value)) {
        return type;
      }
    }
    return null;
  }
  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }
  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  void executeBlock(List<Stmt> statements,
                    Environment environment) {
    Environment previous = this.environment;
    try {
      this.environment = environment;

      for (Stmt statement : statements) {
        execute(statement);
      }
    } finally {
      this.environment = previous;
    }
  }
  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    executeBlock(stmt.statements, new Environment(environment));
    return null;
  }
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    MlangFunction function = new MlangFunction(stmt, environment);
    environment.define(stmt.name.lexeme, function, "FUNCTION_TYPE");
    return null;
  }
  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    if (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      execute(stmt.elseBranch);
    }
    return null;
  }
  @Override
  public Void visitScrnoutStmt(Stmt.Scrnout stmt) {
    for (Expr argument : stmt.arguments) {
      println(stringify(evaluate(argument)));
    }
    return null;
  }
  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    Object value = null;
    if (stmt.value != null) {
      value = evaluate(stmt.value);
    }
    if (stmt.condition != null) {
      if (isTruthy(evaluate(stmt.condition))) {
        throw new Return(value);
      } else {
        return null;
      }
    }
    throw new Return(value);
  }
  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    environment.define(stmt.name.lexeme, value, stmt.type.type);
    return null;
  }
  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    while (isTruthy(evaluate(stmt.condition))) {
      execute(stmt.body);
    }
    return null;
  }
  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);
    environment.assign(expr.name, value);
    return value;
  }
  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right); // [left]

    switch (expr.operator.type) {
      case "BANG_EQUAL": return !isEqual(left, right);
      case "EQUAL_EQUAL": return isEqual(left, right);
      case "GREATER":
        checkNumberOperands(expr.operator, left, right);
        return (double)left > (double)right;
      case "GREATER_EQUAL":
        checkNumberOperands(expr.operator, left, right);
        return (double)left >= (double)right;
      case "LESS":
        checkNumberOperands(expr.operator, left, right);
        return (double)left < (double)right;
      case "LESS_EQUAL":
        checkNumberOperands(expr.operator, left, right);
        return (double)left <= (double)right;
      case "MINUS":
        checkNumberOperands(expr.operator, left, right);
        return (double)left - (double)right;
      case "PLUS":
        if (left instanceof Double && right instanceof Double) {
          return (double)left + (double)right;
        } // [plus]

        if (left instanceof String && right instanceof String) {
          return (String)left + (String)right;
        }

        throw new MlangRuntimeError(expr.operator,
            "Operands must be two numbers or two strings.");
      case "SLASH":
        checkNumberOperands(expr.operator, left, right);
        return (double)left / (double)right;
      case "STAR":
        checkNumberOperands(expr.operator, left, right);
        return (double)left * (double)right;
    }

    // Unreachable.
    return null;
  }
  @Override
  public Object visitCallExpr(Expr.Call expr) {
    Object callee = evaluate(expr.callee);

    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) { // [in-order]
      arguments.add(evaluate(argument));
    }

    if (!(callee instanceof MlangCallable)) {
      throw new MlangRuntimeError(expr.paren,
          "Can only call functions and classes.");
    }

    MlangCallable function = (MlangCallable)callee;
    if (arguments.size() != function.arity()) {
      throw new MlangRuntimeError(expr.paren, "Expected " +
          function.arity() + " arguments but got " +
          arguments.size() + ".");
    }

    return function.call(this, arguments);
  }
  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }
  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }
  @Override
  public Object visitLogicalExpr(Expr.Logical expr) {
    Object left = evaluate(expr.left);

    if (expr.operator.type.equals("OR")) {
      if (isTruthy(left)) {
        return true;
      } else {
        return isTruthy(evaluate(expr.right));
      }
    } else if(expr.operator.type.equals("AND")) {
      if (!isTruthy(left)) {
        return false;
      } else {
        return isTruthy(evaluate(expr.right));
      }
    } else if(expr.operator.type.equals("EVALOR")) {
      if (isTruthy(left)) {
        return left;
      } else {
        return evaluate(expr.right);
      }
    } else if(expr.operator.type.equals("EVALAND")) {
      if (!isTruthy(left)) {
        return left;
      } else {
        return evaluate(expr.right);
      }
    }
    throw new MlangRuntimeError(expr.operator, "Unknown operator.");
  }
  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case "BANG":
        return !isTruthy(right);
      case "NEGATIVE":
        checkNumberOperand(expr.operator, right);
        return -(double)right;
    }

    // Unreachable.
    return null;
  }
  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.get(expr.name);
  }
  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new MlangRuntimeError(operator, "Operand must be a number.");
  }
  private void checkNumberOperands(Token operator,
                                   Object left, Object right) {
    // if (left instanceof Double && right instanceof Double) return;
    if ((typeCheck(left, "NUMBER_TYPE") && typeCheck(right, "NUMBER_TYPE"))) return;
    // [operand]
    throw new MlangRuntimeError(operator, "Operands must be numbers.");
  }
  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean)object;
    return true;
  }
  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;

    return a.equals(b);
  }
  private String stringify(Object object) {
    if (object == null) return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }

    return object.toString();
  }
}
