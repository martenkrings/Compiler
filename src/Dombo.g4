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
                | functionCall ';'
                | 'null;'
                ;

varDec          : DATATYPE ID '=' value=expression                                  #VarDeclaration
                | DATATYPE ID                                                       #GenericVarDeclaration
                ;

block           : '{' statement* '}'                                                #Scope
                ;

functionBlock   : '{' statement* returnStatement '}'
                ;

expression      : stringExpression
                | calcExpression
                | logicExpression
                | '(' expression ')'
                | functionCall
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

stringExpression : STRINGVALUE                                                      #StringValue
                | ID                                                                #StringVariable
                | stringExpression '+' expression                                   #StringWithExpression
                | readStatement                                                     #StringReadStatement
                | left=stringExpression '+' right=stringExpression                  #StringAddOp
                ;

ifStatement     : 'if' '(' condition=logicExpression ')' block                          #IfSingleStatement
                | 'if' '(' condition=logicExpression ')' block 'else' block             #IfElseStatement
                | 'if' '(' condition=logicExpression ')' block 'else' ifStatement       #IfElseIfStatement
                ;

whileLoop       : 'while' '(' conditon=logicExpression ')' block                                                                    #While
                ;

forLoop         : 'for' '(' varDec ';' condition=logicExpression ';' varDec ')' block                                               #For
                ;

functionDec     : 'function' returntype=(RETURNTYPE | DATATYPE) name=ID functionTotal  #FunctionDeclaration
                ;

functionTotal   : '(' ((functionParameter ',')* (functionParameter))? ')' functionBlock
                ;

functionParameter: dataType=DATATYPE name=ID                                                                                           #FunctionPara
                ;

functionCall    : 'do' name=ID '(' ((parameters=parameter ',')* (parameters=parameter))? ')'                                           #Function
                ;

globalVarDec    : 'global' varDec                                                       #GlobalDec
                ;

printStatement  : 'print' stringExpression                                              #PrintCommand
                ;

readStatement   : 'readLine'                                                            #ReadCommand
                ;

returnStatement : 'return' returned=expression ';'                                               #ReturnCommand
                ;

parameter       : ID
                | calcExpression
                | logicExpression
                ;

DATATYPE        : 'int' | 'boolean' | 'String';
RETURNTYPE      : 'void';
BOOLEANVALUE    : 'true' | 'false';
STRINGVALUE     : '"' (ID| [ ])* '"';
ID              : [A-Za-z] [A-Za-z0-9]*;
INT             : '0' | [1-9][0-9]*;
WS              : [\r\n\t\f ]+ -> skip ;