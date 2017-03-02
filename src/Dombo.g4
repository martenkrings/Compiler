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
                | returnStatement ';'
                | functionCall ';'
                ;

varDec          : DATATYPE ID '=' expression                                        #VarDeclaration
                | DATATYPE ID                                                       #GenericVarDeclaration
                ;

block           : '{' statement* '}'                                                #Scope
                ;

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

ifStatement     : 'if' '(' condition=logicExpression ')' block                          #IfSingleStatement
                | 'if' '(' condition=logicExpression ')' block 'else' block             #IfElseStatement
                | 'if' '(' condition=logicExpression ')' block 'else' ifStatement       #IfElseIfStatement
                ;

whileLoop       : 'while' '(' conditon=logicExpression ')' block                                                                    #While
                ;

forLoop         : 'for' '(' varDec ';' condition=logicExpression ';' varDec ')' block                                               #For
                ;

functionDec     : 'function' returntype=RETURNTYPE name=ID '(' ((parameters+=DATATYPE ID ',')* (parameters+=DATATYPE ID))? ')' block  #FunctionDeclaration
                ;

functionCall    : 'do' ID '(' ((parameters+=parameter ',')* (parameters+=parameter))? ')'                                           #Function
                ;

globalVarDec    : 'global' varDec                                                       #GlobalDec
                ;

printStatement  : 'print' expression                                                    #PrintCommand
                ;

readStatement   : 'readLine'                                                            #ReadCommand
                ;

returnStatement : 'return' statement                                                    #ReturnCommand
                ;

parameter       : ID
                | calcExpression
                | logicExpression
                ;

DATATYPE        : 'int' | 'boolean';
RETURNTYPE      : DATATYPE | 'void';
BOOLEANVALUE    : 'true' | 'false';
ID              : [A-Za-z] [A-Za-z0-9]*;
INT             : '0' | [1-9][0-9]*;
WS              : [\r\n\t\f ]+ -> skip ;