import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Created by Marten on 2/28/2017.
 */
public class DomboTypeChecker extends DomboBaseVisitor {

    private Scope programScope;

    @Override
    public Object visitProgram(DomboParser.ProgramContext ctx) {
        programScope = new Scope();
        return super.visitProgram(ctx);
    }

    @Override
    public Object visitStatement(DomboParser.StatementContext ctx) {
        return super.visitStatement(ctx);
    }

    @Override
    public Object visitVarDeclaration(DomboParser.VarDeclarationContext ctx) {
        return super.visitVarDeclaration(ctx);
    }

    @Override
    public Object visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        return super.visitGenericVarDeclaration(ctx);
    }

    @Override
    public Object visitScope(DomboParser.ScopeContext ctx) {
        return super.visitScope(ctx);
    }

    @Override
    public Object visitExpression(DomboParser.ExpressionContext ctx) {
        return super.visitExpression(ctx);
    }

    @Override
    public Object visitAddOp(DomboParser.AddOpContext ctx) {
        return super.visitAddOp(ctx);
    }

    @Override
    public Object visitNegateOp(DomboParser.NegateOpContext ctx) {
        return super.visitNegateOp(ctx);
    }

    @Override
    public Object visitMulOp(DomboParser.MulOpContext ctx) {
        return super.visitMulOp(ctx);
    }

    @Override
    public Object visitIntVariable(DomboParser.IntVariableContext ctx) {

        return super.visitIntVariable(ctx);
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