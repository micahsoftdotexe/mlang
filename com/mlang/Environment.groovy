package com.mlang;

import java.util.HashMap;
import java.util.Map;

class Environment {
  final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();
  private final Map<String, String> types = new HashMap<>();
  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }

    if (enclosing != null) return enclosing.get(name);

    throw new MlangRuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      if (Interpreter.typeCheck(value, types.get(name.lexeme))) {
        values.put(name.lexeme, value);
      } else {
        throw new MlangTypeError(
            "Type '" + DataType.DATA_FULL_NAME_TYPE.get(Interpreter.typeLookup(value)) + "' is not assignable to type '" + DataType.DATA_FULL_NAME_TYPE.get(types.get(name.lexeme)) + "'.");
      }
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }

    throw new MlangRuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  void define(String name, Object value, String type) {
    if (Interpreter.typeCheck(value, type)) {
      values.put(name, value);
      types.put(name, type);
    } else {
      throw new MlangTypeError(
          "Type '" + DataType.DATA_FULL_NAME_TYPE.get(Interpreter.typeLookup(value)) + "' is not assignable to type '" + DataType.DATA_FULL_NAME_TYPE.get(type) + "'.");
    }
  }
  @Override
  public String toString() {
    String result = values.toString();
    if (enclosing != null) {
      result += " -> " + enclosing.toString();
    }

    return result;
  }
}
