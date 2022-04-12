package com.mlang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// import static com.mlang.TokenType; // [static-import]

class Scanner {
  private List<String> lines;
  private List<Token> tokens;
  private int line;

  Scanner(String source) {
    String[] linesArray = source.split("\n");
    lines = new ArrayList<String>(Arrays.asList(linesArray));
    tokens = new ArrayList<Token>();
    line = 1;
  }

  public List<Token> scanTokens() {
    for (String line : lines) {
      String tempLine = line;
      while (!tempLine.isEmpty() && tempLine != "\n") {
        String[] token = scanFirst(tempLine);
        println(token.getAt(1)+token.getAt(0));
      }
    }
  }

  private String[] scanFirst(String line) {
    String[] possibleToken = null;
    // for (TokenType lexeme : TokenType.LITERAL_TOKEN_TYPES.values()) {
    //   if (line.startsWith(lexeme)) {
    //     if (!possibleToken.isEmpty() && lexeme.length() > possibleToken.get(0)) {
    //      possibleToken.add([lexeme, type.getValue()]);
    //     }

    //   }
    // }
    TokenType.LITERAL_TOKEN_TYPES.forEach((lexeme, type) -> {
      if (line.startsWith(lexeme)) {
        if (possibleToken == null || lexeme.length() > possibleToken.get(0)) {
          possibleToken = [lexeme, type];
        }
      }
    });
    if (possibleToken == null) {
      TokenType.REGEX_TYPES.forEach((type, regex) -> {
        Pattern pattern = Pattern.compile("^"+regex);
        Matcher match = pattern.matcher(line);
        if (match.find()) {
          possibleToken = [match.group(0), type];

        }

      });
      if (possibleToken != null && possibleToken.getAt(1) == "IDENTIFIER") {
        TokenType.RESERVED_WORDS.forEach((lexeme, type) -> {
          if (lexeme == possibleToken.getAt(0)) {
            possibleToken = [lexeme, type];
          }

        });
      }
    }
    return possibleToken;
  }
}
