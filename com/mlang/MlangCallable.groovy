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
  public String returnType() {
    // println("Type: " + declaration.type.type);
    return declaration.type.type;
  }
  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme +"\\" + declaration.type.lexeme + ">";
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
      if(Interpreter.typeCheck(arguments.get(i), declaration.params.get(i).get(1).type)) {
        environment.define(declaration.params.get(i).getAt(0).lexeme, arguments.get(i), Interpreter.typeLookup(arguments.get(i)));
      } else {
        throw new MlangTypeError("Type '" + DataType.DATA_FULL_NAME_TYPE.get(declaration.params.get(i).get(1).type) + "' is not compatible with '" + DataType.DATA_FULL_NAME_TYPE.get(Interpreter.typeLookup(arguments.get(i))) + "'");
      }
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      if (Interpreter.typeCheck(returnValue.value, this.returnType())) {
        return returnValue.value;  
      } else {
        throw new MlangTypeError(
            "Function '" + declaration.name.lexeme + "' does not return a value of type " + DataType.DATA_FULL_NAME_TYPE.get(this.returnType()) +  ".");
      }
    }
    return null;
  }
}
