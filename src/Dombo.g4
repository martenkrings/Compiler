grammar Dombo;

program         : (statement | functionDec)* startFunctionDec (statement | functionDec)*
                ;

startFunctionDec: 'START function void main()' '{' statement* 'return void;' '}'
                ;

statement       : varDec ';'
                | ifStatement
                | whileLoop
                | forLoop
                | printStatement ';'
                | readStatement ';'
                | functionCall ';'
                | variableAssign ';'
                ;



varDec          : DATATYPE ID '=' value=expression                                  #VarDeclaration
                | DATATYPE ID                                                       #GenericVarDeclaration
                ;

block           : '{' statement* returnStatement? '}'
                ;

expression      : calcExpression
                | logicExpression
                | '(' expression ')'
                | functionCall
                | stringExpression
                ;

variableAssign: name=ID '=' value=expression;

calcExpression  : INT                                                               #IntValue
                | 'I' ID                                                                #IntVariable
                | '-' calcExpression                                                #NegateOp
                | left=calcExpression op=('*' | '/' | '%') right=calcExpression     #MulOp
                | left=calcExpression op=('+' | '-') right=calcExpression           #AddOp
                ;

logicExpression : BOOLEANVALUE                                                                                  #BoolValue
                | 'B' ID                                                                                            #BoolVariable
                | leftLogic=logicExpression op=('==' | '!=') rightLogic=logicExpression                         #LogicComparator
                | leftCalc=calcExpression op=('<' | '>' | '>=' | '<=' | '==' | '!=') rightCalc=calcExpression   #CalcComparator
                | ('!' | 'not') '(' logicExpression ')'                                                         #NotOp
                ;

stringExpression : STRINGVALUE                                                      #StringValue
                | 'S' ID                                                                #StringVariable
                | left=stringExpression 'append' right=stringExpression                  #StringAddOp
                | stringExpression 'append' expression                                   #StringWithExpression
                | readStatement                                                     #StringReadStatement
                ;

ifStatement     : 'if' '(' condition=logicExpression ')' block                          #IfSingleStatement
                | 'if' '(' condition=logicExpression ')' block 'else' block             #IfElseStatement
                | 'if' '(' condition=logicExpression ')' block 'else' ifStatement       #IfElseIfStatement
                ;

whileLoop       : 'while' '(' condition=logicExpression ')' block                                                                    #While
                ;

forLoop         : 'for' '(' vardec=varDec ';' condition=logicExpression ';' variableAssign ')' block                                 #For
                ;

functionDec     : 'function' returntype=(RETURNTYPE | DATATYPE) name=ID '(' ((functionParameter ',')* (functionParameter))? ')' '{' statement* returnStatement '}'   #FunctionDeclaration
                ;

functionParameter: dataType=DATATYPE name=ID                                                                                           #FunctionPara
                ;

functionCall    : 'do' name=ID '(' ((parameters=parameter ',')* (parameters=parameter))? ')'                                           #Function
                ;

printStatement  : 'print' stringExpression                                              #PrintCommand
                ;

readStatement   : 'readLine'                                                            #ReadCommand
                ;

returnStatement : 'return'expression ';'                                                #ReturnCommand
                | 'return void;'                                                        #ReturnVoidCommand
                ;

parameter       : stringExpression                                                      #StringParameter
                | calcExpression                                                        #CalcParameter
                | logicExpression                                                       #LogicParameter
                ;

DATATYPE        : 'int' | 'boolean' | 'String';
RETURNTYPE      : 'void';
BOOLEANVALUE    : 'true' | 'false';
STRINGVALUE     : '"' ~["]* '"';
ID              : [A-Za-z] [A-Za-z0-9]*;
INT             : '0' | [1-9][0-9]*;
WS              : [\r\n\t\f ]+ -> skip ;