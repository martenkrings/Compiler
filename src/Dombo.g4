grammar Dombo;

program         : statement* 'START' functionDec statement*
                ;

statement       : varDec ';'
                | block
                | ifStatement
                | whileLoop
                | forLoop
                | functionDec
                | globalVarDec ';'
                | printStatement ';'
                | readStatement ';'
                | 'return' expression ';'
                | functionCall ';'
                ;

varDec          : dataType ID '=' expression;

block           : '{' statement* '}';

dataType        : 'int' | 'boolean' | 'void';

expression      : calcExpression
                | logicExpression
                | '(' expression ')'
                | functionCall
                | readStatement
                ;

calcExpression  : INT
                | ID
                | '-' calcExpression
                | left=calcExpression op=('*' | '/' | '%') right=calcExpression
                | left=calcExpression op=('+' | '-') right=calcExpression
                ;

logicExpression : leftLogic=logicExpression op=('==' | '!=' | 'and' | 'or') rightLogic=logicExpression
                | leftCalc=calcExpression op=('<' | '>' | '>=' | '<=' | '==' | '!=') rightCalc=calcExpression
                | ('!' | 'not') logicExpression
                | ('true' | 'false')
                | ID
                ;

ifStatement     : 'if' '(' logicExpression ')' block
                | 'if' '(' logicExpression ')' block 'else' block
                ;

whileLoop       : 'while' '(' logicExpression ')' block
                ;

forLoop         : 'for' '(' varDec ';' logicExpression ';' varDec ')' block
                ;

functionDec     : 'function' dataType ID '(' ((dataType ID ',')* (dataType ID))? ')' block
                ;

functionCall    : 'do' ID '(' ((parameter ',')* (parameter))? ')'
                ;

globalVarDec    : 'global' dataType ID '=' expression
                ;

printStatement  : 'print' expression
                ;

readStatement   : 'readLine'
                ;

parameter       : ID | calcExpression | logicExpression;

ID              : [A-Za-z] [A-Za-z0-9]*;
INT             : '0' | [1-9][0-9]*;
WS              : [\r\n\t\f ]+ -> skip ;