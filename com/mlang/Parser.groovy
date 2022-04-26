package com.mlang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mlang.TokenType.*;

class Parser {
  private static class ParseError extends RuntimeException {}

  private final List<Token> tokens;
  public  List<MlangParseError> errors;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
    errors = new ArrayList<MlangParseError>();
  }
  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      try {
        statements.add(declaration());
      } catch (MlangParseError error) {
        errors.add(error);
        return;
      }
    }

    return statements; // [parse-error-handling]
  }
  private Expr expression() {
    return assignment();
  }
  private Stmt declaration() {
    // try {
    if (match("FUNCT")) return function("function");
    if (match("POST")) return varDeclaration();

    return statement();
    // } catch (ParseError error) {
      // synchronize();
      // return;
    // }
  }
  private Stmt statement() {
    if (match("FOR")) return forStatement();
    if (match("IF")) return ifStatement();
    if (match("PRINT")) return scrnoutStatement();
    if (match("RETURN")) return returnStatement();
    if (match("WHILE")) return whileStatement();
    if (match("LEFT_BRACE")) return new Stmt.Block(block());

    return expressionStatement();
  }
  private Stmt forStatement() {
    consume("LEFT_PAREN", "Expect '(' after 'for'.");

    Stmt initializer;
    if (match("SEMICOLON")) {
      initializer = null;
    } else if (match("POST")) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check("SEMICOLON")) {
      condition = expression();
    }
    consume("SEMICOLON", "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check("RIGHT_PAREN")) {
      increment = expression();
    }
    consume("RIGHT_PAREN", "Expect ')' after for clauses.");
    Stmt body = statement();

    if (increment != null) {
      body = new Stmt.Block(
          Arrays.asList(
              body,
              new Stmt.Expression(increment)));
    }

    if (condition == null) condition = new Expr.Literal(true);
    body = new Stmt.While(condition, body);

    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }

    return body;
  }
  private Stmt ifStatement() {
    consume("LEFT_PAREN", "Expect '(' after 'if'.");
    Expr condition = expression();
    consume("RIGHT_PAREN", "Expect ')' after if condition."); // [parens]

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match("ELSE")) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }
  private Stmt scrnoutStatement() {
    List<Expr> arguments = new ArrayList<>();
    consume("LEFT_PAREN", "Expect '(' after 'scrnout'.");
    if (!check("RIGHT_PAREN")) {
      do {
        if (arguments.size() >= 255) {
          // error(peek(), "Can't have more than 255 arguments.");
          // MlangParseError.error(peek(), "Can't have more than 255 arguments.");
          throw new MlangParseError(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match("COMMA"));
    }
    // Expr value = expression();
    consume("RIGHT_PAREN", "Expect ')' after arguments in scrnout.");
    consume("SEMICOLON", "Expect ';' after scrnout statement.");
    return new Stmt.Scrnout(arguments);
  }
  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    Expr condition  = null;
    if (!check("SEMICOLON")) {
      consume("LEFT_PAREN", "Expect '(' after 'return'.");
      value = expression();
      if (match("IF")) {
        // consume("LEFT_PAREN", "Expect '(' after 'if' in return.");
        condition = expression();
        // consume("RIGHT_PAREN", "Expect ')' after return condition.");
      }
      consume("RIGHT_PAREN", "Expect ')' after if condtion.");


    }

    consume("SEMICOLON", "Expect ';' after return value.");
    return new Stmt.Return(keyword, value, condition);
  }
  private Stmt varDeclaration() {
    Token name = consume("IDENTIFIER", "Expect variable name.");
    consume("BACKSLASH", "Expect '\\' after variable name.");
    Token type = consume("TYPES", "Expect variable type.");
    Expr initializer = null;
    if (match("ASSIGN")) {
      initializer = expression();
    }

    consume("SEMICOLON", "Expect ';' after variable declaration.");
    return new Stmt.Var(name, type, initializer);
  }
  private Stmt whileStatement() {
    consume("LEFT_PAREN", "Expect '(' after 'while'.");
    Expr condition = expression();
    consume("RIGHT_PAREN", "Expect ')' after condition.");
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }
  private Stmt expressionStatement() {
    Expr expr = expression();
    consume("SEMICOLON", "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }
  private Stmt.Function function(String kind) {
    Token name = consume("IDENTIFIER", "Expect " + kind + " name.");
    consume("BACKSLASH", "Expect '\\' after " + kind + " name.");
    Token type = consume("TYPES", "Expect " + kind + " type.");
    consume("LEFT_PAREN", "Expect '(' after " + kind + " name.");
    List<List<Token>> parameters = new ArrayList<>();
    if (!check("RIGHT_PAREN")) {
      do {
        if (parameters.size() >= 255) {
          // error(peek(), "Can't have more than 255 parameters.");
          throw new MlangParseError(peek(), "Can't have more than 255 parameters.");
        }
        Token identifier = consume("IDENTIFIER", "Expect parameter name.");
        consume("BACKSLASH", "Expect '\\' after parameter name.");
        Token paramtype = consume("TYPES", "Expect parameter type.");
        parameters.add([identifier, paramtype]);
        // parameters.add(
        //     consume("IDENTIFIER", "Expect parameter name.",));
      } while (match("COMMA"));
    }
    consume("RIGHT_PAREN", "Expect ')' after parameters.");

    consume("LEFT_BRACE", "Expect '{' before " + kind + " body.");
    List<Stmt> body = block();
    return new Stmt.Function(name, type, parameters, body);
  }
  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check("RIGHT_BRACE") && !isAtEnd()) {
      statements.add(declaration());
    }

    consume("RIGHT_BRACE", "Expect '}' after block.");
    return statements;
  }
  private Expr assignment() {
    Expr expr = or();

    if (match("ASSIGN")) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }

      // error(equals, "Invalid assignment target."); // [no-throw]
      throw new MlangParseError(equals, "Invalid assignment target.");
    }

    return expr;
  }
  private Expr or() {
    Expr expr = and();

    while (match("OR") || match("EVALOR")) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }
  private Expr and() {
    Expr expr = equality();

    while (match("AND") || match("EVALAND")) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }
  private Expr equality() {
    Expr expr = comparison();

    while (match("BANG_EQUAL", "EQUAL_EQUAL")) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr comparison() {
    Expr expr = term();

    while (match("GREATER", "GREATER_EQUAL", "LESS", "LESS_EQUAL")) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr term() {
    Expr expr = factor();

    while (match("MINUS", "PLUS")) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr factor() {
    Expr expr = unary();

    while (match("SLASH", "STAR")) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
  private Expr unary() {
    if (match("BANG", "MINUS")) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return call();
  }
  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check("RIGHT_PAREN")) {
      do {
        if (arguments.size() >= 255) {
          //error(peek(), "Can't have more than 255 arguments.");
          throw new MlangParseError(peek(), "Can't have more than 255 arguments.");
        }
        arguments.add(expression());
      } while (match("COMMA"));
    }

    Token paren = consume("RIGHT_PAREN",
                          "Expect ')' after arguments.");

    return new Expr.Call(callee, paren, arguments);
  }
  private Expr call() {
    Expr expr = primary();

    while (true) { // [while-true]
      if (match("LEFT_PAREN")) {
        expr = finishCall(expr);
      } else {
        break;
      }
    }

    return expr;
  }
  private Expr primary() {
    if (match("FALSE")) return new Expr.Literal(false);
    if (match("TRUE")) return new Expr.Literal(true);
    if (match("NIL")) return new Expr.Literal(null);

    if (match("NUMBER", "STRING")) {
      return new Expr.Literal(previous().literal);
    }

    if (match("IDENTIFIER")) {
      return new Expr.Variable(previous());
    }

    if (match("LEFT_PAREN")) {
      Expr expr = expression();
      consume("RIGHT_PAREN", "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    // throw error(peek(), "Expect expression.");
    throw new MlangParseError(peek(), "Expect expression.");
  }
  private boolean match(String... types) {
    for (String type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }
  private Token consume(String type, String message) {
    if (check(type)) return advance();

    // throw error(peek(), message);
    throw new MlangParseError(peek(), message);
  }
  private boolean check(String type) {
    if (isAtEnd()) return false;
    String currentType = peek().type;
    if (type == "TYPES") {
      return peek().lexeme in DataType.DATA_TYPE_NAMES;
    }

    return currentType == type;
  }
  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }
  private boolean isAtEnd() {
    return peek().type == "EOF";
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token next() {
    if (current + 1 > tokens.size() - 1) return null;
    return tokens.get(current + 1);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }
  // private ParseError error(Token token, String message) {
  //   Mlang.error(token, message);
  //   return new ParseError();
  // }
  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == "SEMICOLON") return;
      switch (peek().type) {
        case "CLASS":
        case "FUN":
        case "POST":
        case "FOR":
        case "IF":
        case "WHILE":
        case "PRINT":
        case "RETURN":
          return;
      }

      advance();
    }
  }
}
