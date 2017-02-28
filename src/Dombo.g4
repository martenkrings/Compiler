grammar Dombo;

program         : statement* 'START' functionDec statement*
                ;

statement       : varDec ';'                    #VariableDeclaration
                | block                         #CodeBlock
                | ifStatement                   #If
                | whileLoop                     #While
                | forLoop                       #For
                | functionDec                   #FunctionDeclaration
                | globalVarDec ';'              #GlobalVariableDeclaration
                | printStatement ';'            #Print
                | readStatement ';'             #Read
                | 'return' expression ';'       #Return
                | functionCall ';'              #FunctionCaller
                ;

varDec          : DATATYPE ID '=' expression;

block           : '{' statement* '}';

expression      : calcExpression
                | logicExpression
                | '(' expression ')'
                | functionCall
                | readStatement
                ;

calcExpression  : INT                                                               #IntValue
                | ID                                                                #IntVariable
                | '-' calcExpression                                                #NegateOp
                | left=calcExpression op=('*' | '/' | '%') right=calcExpression     #MulOp
                | left=calcExpression op=('+' | '-') right=calcExpression           #AddOp
                ;

logicExpression : leftLogic=logicExpression op=('==' | '!=' | 'and' | 'or') rightLogic=logicExpression          #LogicComparator
                | leftCalc=calcExpression op=('<' | '>' | '>=' | '<=' | '==' | '!=') rightCalc=calcExpression   #CalcComparator
                | ('!' | 'not') logicExpression                                                                 #NotOp
                | BOOLEANVALUE                                                                                  #BoolValue
                | ID                                                                                            #BoolVariable
                ;

ifStatement     : 'if' '(' condition=logicExpression ')' block
                | 'if' '(' condition=logicExpression ')' block 'else' block
                ;

whileLoop       : 'while' '(' conditon=logicExpression ')' block
                ;

forLoop         : 'for' '(' varDec ';' condition=logicExpression ';' varDec ')' block
                ;

functionDec     : 'function' DATATYPE ID '(' ((DATATYPE ID ',')* (DATATYPE ID))? ')' block
                ;

functionCall    : 'do' ID '(' ((parameter ',')* (parameter))? ')'
                ;

globalVarDec    : 'global' DATATYPE ID '=' expression
                ;

printStatement  : 'print' expression
                ;

readStatement   : 'readLine'
                ;

parameter       : ID | calcExpression | logicExpression;

DATATYPE        : 'int' | 'boolean' | 'void';
BOOLEANVALUE    : 'true' | 'false';
ID              : [A-Za-z] [A-Za-z0-9]*;
INT             : '0' | [1-9][0-9]*;
WS              : [\r\n\t\f ]+ -> skip ;