package com.mlang;

class LoxError {
    public String message;
    private int line_number;

    LoxError(int line_number, String message) {
        this.message = message;
        this.line_number = line_number;
    }

    public void report() {
        System.err.println(
            "[line " + this.line_number + "] Error" + where + ": " + message);
        // hadError = true;
    }
}