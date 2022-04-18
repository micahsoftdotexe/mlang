package com.mlang;

import java.util.List;

interface MlangCallable {
  int arity();
  Object call(Interpreter interpreter, List<Object> arguments);
}

class MlangFunction implements MlangCallable {
  private final Stmt.Function declaration;
  private final Environment closure;

  MlangFunction(Stmt.Function declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }
  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme +" \\" + declaration.type.lexeme + ">";
  }
  @Override
  public int arity() {
    return declaration.params.size();
  }
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme,
          arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      //TODO: typecheck here!
      return returnValue.value;
    }
    return null;
  }
}
