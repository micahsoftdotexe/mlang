# M (The Bane of Prof O's Existence)
The M language is based on the lox language but with the following alterations/additions:
* Statically typed (boolean: boo, Number: num, string: str, nil/void: emp)
* Short circuit but two types of and/or andeval/oreval to return evaluated side, and/or to return boolean
* Statically typed functions (referring to their return type)
* Assignment operator will be "<-"
* Checking equality will be using the following symbol: "<==>"
* Embedded return/break conditions
* Print is now `scrnout()`. It can take up to 255 params and each will be printed to a separate line
* Input function (`in()`) to read a line of user input.

The language will be implemented in a AST tree walk interpreter that will be completed up to and including functions. PLEASE DO NOT TAKE THIS AS A SERIOUS LANGUAGE.

## Examples:

* `post one\num <- 1.`; 
  * Assign a 1 to the variable named "one".
* `funct eq(num1\num, num2\num)\boo{ret(1 if (num1 <=> num2)). ret(0).}`
  * A function that returns a '1' if the first number is equal to the second number and a '0' if not. This demonstrates the embedded return conditions.
* `scrnout("Hello World");`
  * The obligatory "Hello World" statement.

## Grammar

```
program        ::= declaration * EOF
declaration    ::= funDecl | varDecl | statement
funDecl        ::= "funct" function
varDecl        ::= "post" IDENTIFIER"\"TYPE ("<-" expression)? ";"
statement      ::= exprStmt | forStmt| ifStmt | scrnoutStmt| returnStmt| whileStmt| block
exprStmt       ::= expression ";"
forStmt        ::= "for" "(" ( varDecl | exprStmt | ";" )
                           expression? ";"
                           expression? ")" statement
ifStmt         ::= "if" "(" expression ")" statement
                 ( "else" statement )?
scrnoutStmt    ::= "scrnout" "(" arguments ")" ";"
returnStmt     ::= "ret" ("("(expression | expression "if" "(" expression ")")? ")")?";"
whileStmt      ::= "while" "(" expression ")" statement
block          ::= "{" declaration* "}"
expression     ::= assignment
assignment     ::= IDENTIFIER "<-" assignment | or
or             ::= and ( ( "or" | "evalor") and )*
and            ::= equality ( ( "and" | "evaland") equality )*
equality       ::= comparison ( ( "<!=>" | "<==>" ) comparison )*
comparison     ::= term ( ( "gt" | "gte" | "lt" | "lte" ) term )*
term           ::= factor ( ( "-" | "+" ) factor )*
factor         ::= unary ( ( "/" | "*" ) unary )*
unary          ::= ( "not" | "neg" ) unary | call
call           ::= primary ("(" arguments? ")")*
primary        ::= "true" | "false" | "null"
               | NUMBER | STRING | IDENTIFIER | TYPE | "(" expression ")"
function       ::= IDENTIFIER "\" TYPE "(" parameters? ")" block
parameters     ::= IDENTIFIER "\" TYPE ( "," IDENTIFIER "\" TYPE )*
arguments      ::= expression ( "," expression )*
NUMBER         ::= DIGIT+ ( "." DIGIT+ )?
IDENTIFIER     ::= ALPHA ( ALPHA | DIGIT )*
ALPHA          ::= [a-z] | [A-Z] | "_"
DIGIT          ::= [0-9]
TYPE           ::= ("boo" | "num" | "str" | "fun" | "emp")
STRING         ::= '"'[^\]*'"'

```

## How to Run
- Build `make build`
- Run REPL `make run`
- Cleanup class files `make clean`
- Build and run REPL `make fullrun`
- Run with file `make run ARGS="path"` (you can put other arguments in the ARGS variable after the file path)
- Run REPL with args `make run ARGS="argument` (make sure the first argument is not a filepath or it will run that file).
- You can replace `run` with `runfull` in the last two commands to get their desired effects.

## Error types
Errors from the interpreter are color coded for easier understanding. Warning: These colors may not show up in all terminals.

1. Red: Scanner Error
2. Yellow: Parser Error
3. Blue: Interpreter Error
4. Purple: Type Error
