//> Appendix II stmt
package com.mlang;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitScrnoutStmt(Scrnout stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
  }

  // Nested Stmt classes here...
//> stmt-block
  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }
//< stmt-block
//> stmt-expression
  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
//< stmt-expression
//> stmt-function
  static class Function extends Stmt {
    Function(Token name, Token type, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.type = type;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final Token type;
    final List<Token> params;
    final List<Stmt> body;
  }
//< stmt-function
//> stmt-if
  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
//< stmt-if
//> stmt-print
  static class Scrnout extends Stmt {
    Scrnout(List<Expr> arguments) {
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitScrnoutStmt(this);
    }

    final List<Expr> arguments;
  }
//< stmt-print
//> stmt-return
  static class Return extends Stmt {
    Return(Token keyword, Expr value, Expr condition) {
      this.keyword = keyword;
      this.value = value;
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
    final Expr condition;
  }
//< stmt-return
//> stmt-var
  static class Var extends Stmt {
    Var(Token name, Token type, Expr initializer) {
      this.name = name;
      this.type = type;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Token type;
    final Expr initializer;
  }
//< stmt-var
//> stmt-while
  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }
//< stmt-while

  abstract <R> R accept(Visitor<R> visitor);
}
//< Appendix II stmt
