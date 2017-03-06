import org.antlr.v4.runtime.tree.ParseTree;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Marten on 2/28/2017.
 */
public class DomboTypeChecker extends DomboBaseVisitor<DataType> {
    private Stack<Scope> scopes;

    /**
     * Finds a variable or method accessible from current scope
     *
     * @param ctxId name of variable
     * @return Type if found else null
     */
    public Type lookUpVariableInScopes(String ctxId) {
        Scope searchingScope = scopes.peek();
        Symbol foundSymbol;

        while (true) {
            //if searching scope equals null then no matching id is found
            if (searchingScope == null) {
                return null;
            }

            //search scope for the symbol
            foundSymbol = searchingScope.lookUpVariable(ctxId);

            //if no symbol is found try the next parent scope
            if (foundSymbol == null) {
                searchingScope = searchingScope.getParentScope();
            } else {
                return foundSymbol.type;
            }
        }
    }

    /**
     * Compares 2 dataTypes
     * @param d1 first dataType to compare
     * @param d2 second dataType(String) to compare
     * @return true if of same type else false
     */
    public boolean compareDataTypes(DataType d1, String d2){
        return d1.getType().equalsIgnoreCase(d2);
    }

    @Override
    public DataType visitProgram(DomboParser.ProgramContext ctx) {
        //start up
        scopes = new Stack<>();
        scopes.add(new Scope());

        //visit function declarations first
        for (int i = 0; i < ctx.statement().size(); i++) {
            visit(ctx.statement().get(i).functionDec());
        }

        //return 'something'
        return super.visitProgram(ctx);
    }

    @Override
    public DataType visitStatement(DomboParser.StatementContext ctx) {
        //Visit children
        return super.visitStatement(ctx);
    }

    @Override
    public DataType visitVarDeclaration(DomboParser.VarDeclarationContext ctx) {
        //Get declared dataType and actual dataType
        String datatype = ctx.DATATYPE().getText();
        DataType actualDataType = visit(ctx.value);

        //Compare declared and actual dataType
        if (actualDataType != null) {
            if (!compareDataTypes(actualDataType, datatype)) {
                try {
                    throw new TypeError(datatype + " and " + actualDataType.getType() + " type mismatch");
                } catch (TypeError typeError) {
                    typeError.printStackTrace();
                }
            }
        }

        //Add variable
        scopes.peek().declareVariable(ctx.ID().getText(), actualDataType);
        super.visitVarDeclaration(ctx);

        return actualDataType;
    }

    @Override
    public DataType visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        //Add a new variable to the current scope
        scopes.peek().declareVariable(ctx.ID().getText(), new DataType(ctx.DATATYPE().getText()));

        //Visit children
        return super.visitGenericVarDeclaration(ctx);
    }


    @Override
    public DataType visitScope(DomboParser.ScopeContext ctx) {
        //Make a new scope and add it to the stack
        scopes.add(new Scope(scopes.peek()));

        //find all functions in this scope
        for (int i = 0; i < ctx.statement().size(); i++) {
            visit(ctx.statement().get(i).functionDec());
        }

        //Visit children
        super.visitScope(ctx);

        //close scope
        scopes.pop();

        //return null, scope is typeLess
        return null;
    }

    @Override
    public DataType visitExpression(DomboParser.ExpressionContext ctx) {
        //Visit Children
        return super.visitExpression(ctx);
    }

    @Override
    public DataType visitAddOp(DomboParser.AddOpContext ctx) {
        //Get left and right dataTypes by visiting children
        DataType leftDataType = visit(ctx.left);
        DataType rightDataType = visit(ctx.right);

        //Check if mul operator is being applied to a boolean if so throw new TypeError
        if (compareDataTypes(leftDataType, DataTypeEnum.BOOLEAN.toString()) | compareDataTypes(rightDataType, DataTypeEnum.BOOLEAN.toString())){
            try {
                throw new TypeError(DataTypeEnum.BOOLEAN.toString() + " not compatible with AddOp");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //compare types
        if (!compareDataTypes(leftDataType, rightDataType.getType())) {
            //throw error if types missmatch
            try {
                throw new TypeError(leftDataType.getType() + " and " + rightDataType.getType() + " type missmatch");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }

        }
        return leftDataType;
    }

    @Override
    public DataType visitNegateOp(DomboParser.NegateOpContext ctx) {
        //Visit children
        DataType dataType = super.visitNegateOp(ctx);
        if (!compareDataTypes(dataType, DataTypeEnum.INT.toString())) {
            try {
                throw new TypeError("negate op only usable on INT variables");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }

        }

        //return dataType
        return dataType;
    }

    @Override
    public DataType visitMulOp(DomboParser.MulOpContext ctx) {
        //Get left and right dataTypes by visiting children
        DataType leftDataType = visit(ctx.left);
        DataType rightDataType = visit(ctx.right);

        //Check if mul operator is being applied to a boolean if so throw new TypeError
        if (compareDataTypes(leftDataType, DataTypeEnum.BOOLEAN.toString()) | compareDataTypes(rightDataType, DataTypeEnum.BOOLEAN.toString())){
            try {
                throw new TypeError(DataTypeEnum.BOOLEAN.toString() + " not compatible with MulOperator");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //compare types
        if (!compareDataTypes(leftDataType, rightDataType.getType())) {
            //throw error if types missmatch
            try {
                throw new TypeError(leftDataType.getType() + " and " + rightDataType.getType() + " type missmatch");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        return leftDataType;
    }

    @Override
    public DataType visitIntVariable(DomboParser.IntVariableContext ctx) {
        Type found = lookUpVariableInScopes(ctx.ID().getText());
        if (found == null) {
            try {
                throw new TypeError(ctx.ID().getText() + " not initialised");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //if its a method return the return type
        if (found instanceof MethodType) {
            return ((MethodType) found).getReturnType();
        }

        //return the type of the found variable
        return (DataType) found;
    }

    @Override
    public DataType visitIntValue(DomboParser.IntValueContext ctx) {
        //return DataType
        return new DataType(DataTypeEnum.INT);
    }

    @Override
    public DataType visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        //Children handle typeChecking
        return super.visitCalcComparator(ctx);
    }

    @Override
    public DataType visitBoolValue(DomboParser.BoolValueContext ctx) {
        //return DataType
        return new DataType(DataTypeEnum.BOOLEAN);
    }

    @Override
    public DataType visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        //Children handle typeChecking
        return super.visitLogicComparator(ctx);
    }

    @Override
    public DataType visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        DataType found = (DataType) lookUpVariableInScopes(ctx.ID().getText());
        if (found == null) {
            try {
                throw new TypeError(ctx.ID().getText() + " not initialised");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //return the type of the found variable
        return found;
    }

    @Override
    public DataType visitNotOp(DomboParser.NotOpContext ctx) {
        //No type checking needed
        return super.visitNotOp(ctx);
    }

    @Override
    public DataType visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        //No type checking needed
        return super.visitIfSingleStatement(ctx);
    }

    @Override
    public DataType visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        //No type checking needed
        return super.visitIfElseStatement(ctx);
    }

    @Override
    public DataType visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        //No type checking needed
        return super.visitIfElseIfStatement(ctx);
    }

    @Override
    public DataType visitWhile(DomboParser.WhileContext ctx) {
        //No type checking needed
        return super.visitWhile(ctx);
    }

    @Override
    public DataType visitFor(DomboParser.ForContext ctx) {
        //No type checking needed
        return super.visitFor(ctx);
    }

    @Override
    public DataType visitFunctionDeclaration(DomboParser.FunctionDeclarationContext ctx) {
        //Get return types
        DataType returnedDataType = visit(ctx.functionTotal().functionBlock());
        DataType methodReturnType = new DataType(ctx.returntype.getText());

        //If return types don't match throw a TypeError
        if (!compareDataTypes(returnedDataType, methodReturnType.getType())) {
            try {
                throw new TypeError("Incorrect return type, expected " + methodReturnType.getType() + " got " + returnedDataType.getType());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }


        List<DataType> dataTypes = new ArrayList<DataType>() {
        };

        //add dataTypes of parameters
        for (int i = 0; i < ctx.functionTotal().functionParameter().size(); i++) {
            dataTypes.add(visit(ctx.functionTotal().functionParameter().get(i)));
        }

        //declare new method
        scopes.peek().declareMethod(ctx.name.getText(), new DataType(ctx.returntype.getText()), dataTypes);

        //visit children
        super.visitFunctionDeclaration(ctx);

        //return returnType
        return methodReturnType;
    }

    @Override
    public DataType visitFunctionTotal(DomboParser.FunctionTotalContext ctx) {
        //make a new scope
        scopes.add(new Scope(scopes.peek()));

        DataType dataType = super.visitFunctionTotal(ctx);

        scopes.pop();

        return dataType;
    }

    @Override
    public DataType visitFunctionPara(DomboParser.FunctionParaContext ctx) {
        //get dataType
        DataType dataType = new DataType(ctx.dataType.getText());

//        //declare new variable
//        scopes.peek().declareVariable(ctx.name.getText(), dataType);

        //return dataType
        return dataType;
    }

    @Override
    public DataType visitFunction(DomboParser.FunctionContext ctx) {
        //loop up function
        Type type = lookUpVariableInScopes(ctx.name.getText());

        //If method is not defined or not a function throw new typeError
        if (type == null | !(type instanceof MethodType)){
            try {
                throw new TypeError("Method " + ctx.name.getText() + " not defined");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            } finally {
                //see if we can typecheck further
                return null;
            }
        }

        //Cast type to methidType
        MethodType methodType = (MethodType) type;

        //Collect parameter Datatypes
        DataType[] dataTypes = new DataType[ctx.parameter().size()];
        for (int i = 0; i < ctx.parameter().size(); i++) {
            dataTypes[i] = visit(ctx.parameter().get(i));
        }

        boolean goodInput = true;

        //check if datTypes given match required parameters of method
        if (dataTypes.length != methodType.getParameters().size()) {
            goodInput = false;
        } else {
            for (int i = 0; i < methodType.getParameters().size(); i++) {
                if (!methodType.getParameters().get(i).getType().equalsIgnoreCase(dataTypes[i].getType())) {
                    goodInput = false;
                }
            }
        }

        //If parameter types don't match throw a TypeError
        if (!goodInput) {
            try {
                //Readable error formatting
                String expected = "(";
                for (int i = 0; i < methodType.getParameters().size(); i++) {
                    expected += methodType.getParameters().get(i).getType();
                    if (methodType.getParameters().size() - 1 != i) {
                        expected += ", ";
                    }
                }
                expected += ")";

                String got = "(";
                for (int i = 0; i < dataTypes.length; i++) {
                    got += dataTypes[i].getType();
                    if (dataTypes.length - 1 != i) {
                        got += ", ";
                    }
                }

                got += ")";

                //Throw new error
                throw new TypeError("Expected: " + expected + " got " + got);
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //return return DataType
        return methodType.getReturnType();
    }

    @Override
    public DataType visitGlobalDec(DomboParser.GlobalDecContext ctx) {

        return super.visitGlobalDec(ctx);
    }

    @Override
    public DataType visitPrintCommand(DomboParser.PrintCommandContext ctx) {
        //TODO is typechecking needed here?
        //Do nothing
        return super.visitPrintCommand(ctx);
    }

    @Override
    public DataType visitReadCommand(DomboParser.ReadCommandContext ctx) {
        //return String dataType
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitParameter(DomboParser.ParameterContext ctx) {
        //visit children
        return super.visitParameter(ctx);
    }

    @Override
    public DataType visitFunctionBlock(DomboParser.FunctionBlockContext ctx) {
        //NOTE: new scope made in functionTotal

        //find all functions in this scope
        for (int i = 0; i < ctx.statement().size(); i++) {
            //In case of empty scope
            if (ctx.statement().get(i).functionDec() != null) {
                visit(ctx.statement().get(i).functionDec());
            }
        }

        //get return dataType
        DataType returnDataType = visit(ctx.returnStatement());

        //visit children
        super.visitFunctionBlock(ctx);

        //return return dataType
        return returnDataType;
    }

    @Override
    public DataType visitReturnCommand(DomboParser.ReturnCommandContext ctx) {
        //get return DataType
        DataType dataType = visit(ctx.returned);

        //return dataType
        return dataType;
    }
}
