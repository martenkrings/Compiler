import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Stack;

/**
 * Created by Marten on 2/28/2017.
 */
public class DomboTypeChecker extends DomboBaseVisitor {

    private Stack<Scope> scopes;

    @Override
    public Object visitProgram(DomboParser.ProgramContext ctx) {
        scopes = new Stack<>();
        scopes.add(new Scope());
        return super.visitProgram(ctx);
    }

    @Override
    public Object visitStatement(DomboParser.StatementContext ctx) {
        //Visit children
        return super.visitStatement(ctx);
    }

    @Override
    public Object visitVarDeclaration(DomboParser.VarDeclarationContext ctx) throws TypeError {
        //Get declared dataType and actual dataType
        String datatype = ctx.DATATYPE().getText();
        DataType actualDataType = (DataType) visit(ctx.value);

        //Compare declared and actual dataType
        if (!actualDataType.getType().equals(datatype)){
            throw new TypeError(datatype + " and " + actualDataType + " type mismatch");

        }

        //Add variable
        scopes.peek().declareVariable(ctx.ID().getText(), actualDataType);
        super.visitVarDeclaration(ctx);

        return actualDataType;
    }

    @Override
    public Object visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        //Add a new variable to the current scope
        scopes.peek().declareVariable(ctx.ID().getText(), new DataType(ctx.DATATYPE().getText()));

        //Visit children
        super.visitGenericVarDeclaration(ctx);

        return null;
    }

    @Override
    public Object visitScope(DomboParser.ScopeContext ctx) {
        //Make a new scope and add it to the stack
        scopes.add(new Scope(scopes.peek()));

        //Visit children
        super.visitScope(ctx);

        //close scope
        scopes.pop();

        //scope doesn't have a type to return
        return null;
    }

    @Override
    public Object visitExpression(DomboParser.ExpressionContext ctx) {
        //Visit Children
        return super.visitExpression(ctx);
    }

    @Override
    public Object visitAddOp(DomboParser.AddOpContext ctx) throws TypeError {
        //Get left and right dataTypes by visiting children
        DataType leftDataType = (DataType) visit(ctx.left);
        DataType rightDataType = (DataType) visit(ctx.right);

        //compare types
        if (!leftDataType.getType().equals(rightDataType.getType())){
            //throw error if types missmatch
            throw new TypeError(leftDataType + " and " + rightDataType + " type missmatch");

        }
        return leftDataType;
    }

    @Override
    public Object visitNegateOp(DomboParser.NegateOpContext ctx) throws TypeError {
        //Visit children
        DataType dataType = (DataType) super.visitNegateOp(ctx);
        if (!dataType.getType().equals(DataTypeEnum.INT.toString())){
            throw new TypeError("negate op only usable on INT variables");

        }

        //return dataType
        return dataType;
    }

    @Override
    public Object visitMulOp(DomboParser.MulOpContext ctx) throws TypeError {
        //Get left and right dataTypes by visiting children
        DataType leftDataType = (DataType) visit(ctx.left);
        DataType rightDataType = (DataType) visit(ctx.right);


        //compare types
        if (!leftDataType.getType().equals(rightDataType.getType())){
            //throw error if types missmatch
            throw new TypeError(leftDataType + " and " + rightDataType + " type missmatch");

        }

        return leftDataType;
    }

    @Override
    public Object visitIntVariable(DomboParser.IntVariableContext ctx) {
        Scope searchingScope = scopes.peek();
        Symbol foundSymbol = null;
        boolean found = false;

        while (!found){
            //if searching scope equals null then no matching id is found
            if (searchingScope == null){
                try {
                    throw new TypeError(ctx.ID().getText() + " not initialized");
                } catch (TypeError typeError) {
                    typeError.printStackTrace();
                }
            }

            //search scope for the symbol
            foundSymbol = searchingScope.lookUpVariable(ctx.ID().getText());

            //if no symbol is found try the next parent scope
            if (foundSymbol == null){
                searchingScope = searchingScope.getParentScope();
            } else {
                found = true;
            }
        }

        //return the type of the found variable
        return foundSymbol.type;
    }

    @Override
    public Object visitIntValue(DomboParser.IntValueContext ctx) {
        return super.visitIntValue(ctx);
    }

    @Override
    public Object visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        return super.visitCalcComparator(ctx);
    }

    @Override
    public Object visitBoolValue(DomboParser.BoolValueContext ctx) {
        return super.visitBoolValue(ctx);
    }

    @Override
    public Object visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        return super.visitLogicComparator(ctx);
    }

    @Override
    public Object visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        return super.visitBoolVariable(ctx);
    }

    @Override
    public Object visitNotOp(DomboParser.NotOpContext ctx) {
        return super.visitNotOp(ctx);
    }

    @Override
    public Object visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        return super.visitIfSingleStatement(ctx);
    }

    @Override
    public Object visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        return super.visitIfElseStatement(ctx);
    }

    @Override
    public Object visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        return super.visitIfElseIfStatement(ctx);
    }

    @Override
    public Object visitWhile(DomboParser.WhileContext ctx) {
        return super.visitWhile(ctx);
    }

    @Override
    public Object visitFor(DomboParser.ForContext ctx) {
        return super.visitFor(ctx);
    }

    @Override
    public Object visitFunctionDeclaration(DomboParser.FunctionDeclarationContext ctx) {
        return super.visitFunctionDeclaration(ctx);
    }

    @Override
    public Object visitFunction(DomboParser.FunctionContext ctx) {
        return super.visitFunction(ctx);
    }

    @Override
    public Object visitGlobalDec(DomboParser.GlobalDecContext ctx) {
        return super.visitGlobalDec(ctx);
    }

    @Override
    public Object visitPrintCommand(DomboParser.PrintCommandContext ctx) {
        return super.visitPrintCommand(ctx);
    }

    @Override
    public Object visitReadCommand(DomboParser.ReadCommandContext ctx) {
        return super.visitReadCommand(ctx);
    }

    @Override
    public Object visitParameter(DomboParser.ParameterContext ctx) {
        return super.visitParameter(ctx);
    }
}
