package com.mlang;

import java.util.HashMap;
// enum TokenType {
//   // Single-character tokens.
//   LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
//   COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

//   // One or two character tokens.
//   BANG, BANG_EQUAL,
//   EQUAL, EQUAL_EQUAL,
//   GREATER, GREATER_EQUAL,
//   LESS, LESS_EQUAL,

//   // Literals.
//   IDENTIFIER, STRING, NUMBER,

//   // Keywords.
//   AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
//   PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

//   EOF
// }
class TokenType{
  public static HashMap<String,String> LITERAL_TOKEN_TYPES;
  public static HashMap<String,String> RESERVED_WORDS;
  public static HashMap<String,String> REGEX_TYPES;
  static {
    LITERAL_TOKEN_TYPES = new HashMap<>();
    LITERAL_TOKEN_TYPES.put("(",    "LEFT_PAREN");
    LITERAL_TOKEN_TYPES.put(")",    "RIGHT_PAREN");
    LITERAL_TOKEN_TYPES.put("{",    "LEFT_BRACE");
    LITERAL_TOKEN_TYPES.put("}",    "RIGHT_BRACE");
    LITERAL_TOKEN_TYPES.put(",",    "COMMA");
    LITERAL_TOKEN_TYPES.put(".",    "DOT"); //semicolon
    LITERAL_TOKEN_TYPES.put("-",    "MINUS");
    LITERAL_TOKEN_TYPES.put("neg",    "NEGATIVE");
    LITERAL_TOKEN_TYPES.put("+",    "PLUS");
    LITERAL_TOKEN_TYPES.put("+=",   "PLUS_EQUAL");
    LITERAL_TOKEN_TYPES.put("-=",   "MINUS_EQUAL");
    LITERAL_TOKEN_TYPES.put("/",    "SLASH");
    LITERAL_TOKEN_TYPES.put("//",    "DOUBLE_SLASH");
    LITERAL_TOKEN_TYPES.put("\\",    "BACKSLASH");  
    LITERAL_TOKEN_TYPES.put("*",    "STAR");
    LITERAL_TOKEN_TYPES.put(";",   "SEMICOLON");
    LITERAL_TOKEN_TYPES.put("not",    "BANG");
    LITERAL_TOKEN_TYPES.put("<!=>",   "BANG_EQUAL");
    LITERAL_TOKEN_TYPES.put("<-",    "ASSIGN"); //Equal
    LITERAL_TOKEN_TYPES.put("<=>",   "EQUAL_EQUAL");
    LITERAL_TOKEN_TYPES.put("gt",    "GREATER");
    LITERAL_TOKEN_TYPES.put("gte",    "GREATER_EQUAL");
    LITERAL_TOKEN_TYPES.put("lt",    "LESS");
    LITERAL_TOKEN_TYPES.put("lte",    "LESS_EQUAL");
    RESERVED_WORDS = new HashMap<>();
    RESERVED_WORDS.put("and",    "AND");
    RESERVED_WORDS.put("evaland",    "EVALAND");
    RESERVED_WORDS.put("class",    "CLASS");
    RESERVED_WORDS.put("else",    "ELSE");
    RESERVED_WORDS.put("false",    "FALSE");
    RESERVED_WORDS.put("funct",    "FUNCT");
    RESERVED_WORDS.put("for",    "FOR");
    RESERVED_WORDS.put("if",    "IF");
    RESERVED_WORDS.put("nil",    "NIL");
    RESERVED_WORDS.put("or",    "OR");
    RESERVED_WORDS.put("post",    "POST");
    RESERVED_WORDS.put("evalor",    "EVALOR");
    RESERVED_WORDS.put("scrnout",    "PRINT");
    RESERVED_WORDS.put("ret",    "RETURN");
    // RESERVED_WORDS.put("boo",    "BOOLEAN_TYPE");
    // RESERVED_WORDS.put("num",    "NUMBER_TYPE");
    // RESERVED_WORDS.put("str",    "STRING_TYPE");
    REGEX_TYPES = new HashMap<>();
    REGEX_TYPES.put("IDENTIFIER",    "[A-Za-z_][A-Za-z0-9_]*");
    REGEX_TYPES.put("STRING",    '"[^"]*"');
    REGEX_TYPES.put("NUMBER",    '[0-9]+(\\.[0-9]+)?');

  }


}


