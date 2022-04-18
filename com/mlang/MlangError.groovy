package com.mlang;

class MlangError extends Error {
    public String message;
    private int line_number;
    private String at = "";

    MlangError(int line_number, String message) {
        this.message = message;
        this.line_number = line_number;
    }

    public void report() {
        System.err.println(
            "[line " + this.line_number + "] Error" + at + ": " + message);
        // hadError = true;
    }
}

class MlangParseError extends Error {
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
            "PARSER: [line " + this.line_number + "] Error" + at + ": " + message);
    }
}