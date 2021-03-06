package com.mlang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.mlang.Token;
import com.mlang.MlangError;

// import static com.mlang.TokenType; // [static-import]

class Scanner {
  private List<String> lines;
  private List<Token> tokens;
  public  List<MlangError> errors;
  private int line;

  Scanner(String source) {
    String[] linesArray = source.split("\n");
    lines = new ArrayList<String>(Arrays.asList(linesArray));
    tokens = new ArrayList<Token>();
    errors = new ArrayList<MlangError>();
    line = 1;
  }

  public List<Token> scanTokens() {
    for (String line : lines) {
      String tempLine = line;
      while (!tempLine.isEmpty()) {
        // get possible token
        String[] token = scanFirst(tempLine);
        // check for error
        MlangError error = checkError(tempLine, token);
        if (error != null) {
          errors.add(error);
          return;
        } else {
          //update tempLine and add tokens
          tempLine = checkToken(tempLine, token);
        }
      }
      if (this.line < lines.size()) {
        this.line++;
      }
    }
    tokens.add(new Token("EOF", "", null, this.line));
    return tokens;
  }



  private String[] scanFirst(String line) {
    String[] possibleToken = null;
    TokenType.LITERAL_TOKEN_TYPES.forEach((lexeme, type) -> {
      if (line.startsWith(lexeme)) {
        if (possibleToken == null || lexeme.length() > possibleToken.getAt(0).length()) {
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
        DataType.DATA_TYPE_NAMES.forEach((lexeme, type) -> {
          if (lexeme == possibleToken.getAt(0)) {
            possibleToken = [lexeme, type];
          }

        });
      }
    }
    return possibleToken;
  }

  private String checkToken(String line, String[] token) {
    if (token == null) {
      return line.substring(1);
    }
    if (token.getAt(0).toUpperCase() == "RICKROLL"){
      java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
    }
    if (TokenType.REGEX_TYPES.containsKey(token.getAt(1))) {
      if (token.getAt(1) == "STRING") {
        tokens.add(new Token(token.getAt(1), token.getAt(0), token.getAt(0).replace('\"', ''), this.line));
        return line.substring(token.getAt(0).length());
      } else if (token.getAt(1) == "NUMBER") {
        tokens.add(new Token(token.getAt(1), token.getAt(0), Double.parseDouble(token.getAt(0)), this.line));
        return line.substring(token.getAt(0).length());
      } else {
        tokens.add(new Token(token.getAt(1), token.getAt(0), null, this.line));
        return line.substring(token.getAt(0).length());
      }
    } else {
      if (token.getAt(1) == "DOUBLE_SLASH") {
        return "";
      }
      tokens.add(new Token(token.getAt(1), token.getAt(0), null, this.line));
      return line.substring(token.getAt(0).length());
    }

  }

  private MlangError checkError(String line, String[] token) {
    if (token == null) {
      
      if (line.startsWith(" ")) {
        return null;
      } else if (line.chars().filter(ch -> ch == '\"').count() % 2 != 0) {
        return new MlangError(this.line, "Unterminated string.");
      } else {
        return new MlangError(this.line, '"'+line.getAt(0)+'" '+"ain't a character I recognize!");
      }
    }
    return null;
  }
}


