import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Stack;

/**
 * Created by Marten on 2/28/2017.
 */
public class DomboTypeChecker extends DomboBaseVisitor<DataType> {

    private Stack<Scope> scopes;

    public DataType lookUpVariableInScopes(String ctxId){
        Scope searchingScope = scopes.peek();
        Symbol foundSymbol;

        while (true){
            //if searching scope equals null then no matching id is found
            if (searchingScope == null){
                return null;
            }

            //search scope for the symbol
            foundSymbol = searchingScope.lookUpVariable(ctxId);

            //if no symbol is found try the next parent scope
            if (foundSymbol == null){
                searchingScope = searchingScope.getParentScope();
            } else {
               return (DataType) foundSymbol.type;
            }
        }
    }

    @Override
    public DataType visitProgram(DomboParser.ProgramContext ctx) {
        scopes = new Stack<>();
        scopes.add(new Scope());
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
        DataType actualDataType = (DataType) visit(ctx.value);

        //Compare declared and actual dataType
        if (actualDataType != null) {
            if (!actualDataType.getType().equalsIgnoreCase(datatype)) {
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

        //Visit children
        DataType returnValue = super.visitScope(ctx);

        //close scope
        scopes.pop();

        //return
        return returnValue;
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

        //compare types
        if (!leftDataType.getType().equalsIgnoreCase(rightDataType.getType())){
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
        DataType dataType = (DataType) super.visitNegateOp(ctx);
        if (!dataType.getType().equalsIgnoreCase(DataTypeEnum.INT.toString())){
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


        //compare types
        if (!leftDataType.getType().equalsIgnoreCase(rightDataType.getType())){
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
        DataType found = lookUpVariableInScopes(ctx.ID().getText());
        if (found == null){
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
    public DataType visitIntValue(DomboParser.IntValueContext ctx) {
        //return DataType
        return new DataType(DataTypeEnum.INT);
    }

    @Override
    public DataType visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        return super.visitCalcComparator(ctx);
    }

    @Override
    public DataType visitBoolValue(DomboParser.BoolValueContext ctx) {
        //return DataType
        return new DataType(DataTypeEnum.BOOLEAN);
    }

    @Override
    public DataType visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        return super.visitLogicComparator(ctx);
    }

    @Override
    public DataType visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        DataType found = lookUpVariableInScopes(ctx.ID().getText());
        if (found == null){
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
        return super.visitNotOp(ctx);
    }

    @Override
    public DataType visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        return super.visitIfSingleStatement(ctx);
    }

    @Override
    public DataType visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        return super.visitIfElseStatement(ctx);
    }

    @Override
    public DataType visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        return super.visitIfElseIfStatement(ctx);
    }

    @Override
    public DataType visitWhile(DomboParser.WhileContext ctx) {
        return super.visitWhile(ctx);
    }

    @Override
    public DataType visitFor(DomboParser.ForContext ctx) {
        return super.visitFor(ctx);
    }

    @Override
    public DataType visitFunctionDeclaration(DomboParser.FunctionDeclarationContext ctx) {
        return super.visitFunctionDeclaration(ctx);
    }

    @Override
    public DataType visitFunction(DomboParser.FunctionContext ctx) {
        return super.visitFunction(ctx);
    }

    @Override
    public DataType visitGlobalDec(DomboParser.GlobalDecContext ctx) {
        return super.visitGlobalDec(ctx);
    }

    @Override
    public DataType visitPrintCommand(DomboParser.PrintCommandContext ctx) {
        return super.visitPrintCommand(ctx);
    }

    @Override
    public DataType visitReadCommand(DomboParser.ReadCommandContext ctx) {
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitParameter(DomboParser.ParameterContext ctx) {
        return super.visitParameter(ctx);
    }

    @Override
    public DataType visitFunctionScope(DomboParser.FunctionScopeContext ctx) {
        return super.visitFunctionScope(ctx);
    }

    @Override
    public DataType visitReturnCommand(DomboParser.ReturnCommandContext ctx) {
        return super.visitReturnCommand(ctx);
    }
}
