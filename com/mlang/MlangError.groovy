package com.mlang;

class MlangError extends Error {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String SCANNER = "\u001B[31m";
    public static final String PARSER = "\u001B[33m";
    public static final String INTERPRETER = "\u001B[34m";
    public static final String TYPE = "\u001B[35m";
    // private static final String ANSI_PURPLE = "\u001B[35m";
    // private static final String ANSI_CYAN = "\u001B[36m";
    // private static final String ANSI_WHITE = "\u001B[37m";

    public String message;
    private int line_number;
    private String at = "";

    MlangError() {
        this.message = "";
        this.line_number = 0;
    }

    MlangError(int line_number, String message) {
        this.message = message;
        this.line_number = line_number;
    }

    public void report() {
        System.err.println(
            SCANNER+"[line " + this.line_number + "] Error" + at + ": " + message + ANSI_RESET);
        // hadError = true;
    }
}

class MlangParseError extends MlangError {
    public String message;
    private int line_number;
    private String at = "";
    MlangParseError(Token token, String message) {
        if (token.type == "EOF") {
            this.at = " at end";
        } else {
            this.at = " at '" + token.lexeme + "'";
        }
        this.line_number = token.line;
        this.message = message;

    }



    public void report() {
        System.err.println(
            PARSER + "[line " + this.line_number + "] Error" + at + ": " + message + ANSI_RESET);
    }
}

class MlangRuntimeError extends MlangError {
    public String message;
    private int line_number;
    MlangRuntimeError(Token token, String message) {
        this.message = message;
        this.line_number = token.line;
    }

    public void report() {
        System.err.println(
            INTERPRETER + "[line " + this.line_number + "] " + message + ANSI_RESET);
    }
}

class MlangTypeError extends MlangError {
    public String message;
    MlangTypeError(String message) {
        this.message = message;
    }

    public void report() {
        System.err.println(
            TYPE + "Type Error: " + message + ANSI_RESET);
    }
}