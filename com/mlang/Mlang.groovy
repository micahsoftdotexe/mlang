package com.mlang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Mlang {
  private static final Interpreter interpreter = new Interpreter();
  public static List<String> cmdargs = new ArrayList<>();
  static boolean hadError = false;
  // static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      if (!(args.length > 255)) {
        for (int i = 0; i < args.length; i++) {
          this.cmdargs.add(args[i]);
        }
      } else {
        println("Too many arguments");
        System.exit(1);
      }
      try {
        Paths.get(args[0]);
        runFile(args[0]);
      } catch(java.nio.file.NoSuchFileException e) {
        runPrompt();
      }
      //cmdargs = args;
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    // Indicate an error in the exit code.
    if (hadError) System.exit(65);
    // if (hadRuntimeError) System.exit(70);
  }
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) { // [repl]
      System.out.print("mlang> ");
      String line = reader.readLine();
      if (line == null) break;
      run(line);
      hadError = false;
    }
  }
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();
    for (MlangError error : scanner.errors) {
      error.report();
      return;
    }
    // for (Token token : tokens) {
    //   System.out.println(token);
    // }

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    // Stop if there was a syntax error.
    for (MlangParseError error : parser.errors) {
      error.report();
      return;
    }

    interpreter.interpret(statements);
  }
  
}
