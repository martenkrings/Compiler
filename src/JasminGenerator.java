import java.util.ArrayList;

/**
 * Created by Sander on 6-3-2017.
 */
public class JasminGenerator extends DomboBaseVisitor<ArrayList<String>> {

    @Override
    public ArrayList<String> visitProgram(DomboParser.ProgramContext ctx) {
        ArrayList<String> code = new ArrayList<>();

//        code.add(".class public ClassName ; Name and access modifier of the class\n" +
//                " .super java/lang/Object ; Inheritance definition\n" +
//                "\n" +
//                " ; Default constructor (empty constructor)\n" +
//                " .method public <init>()V\n" +
//                " aload_0 ; Loads \"this\" on the stack\n" +
//                " invokenonvirtual java/lang/Object/<init>()V ; Call super constructor\n" +
//                " return ; Terminate method\n" +
//                " .end method\n" +
//                "\n" +
//                " ; Method definition for public static void main(String[] args)\n" +
//                " .method public static main([Ljava/lang/String;)V\n" +
//                " .limit stack 2 ; Size of the operand stack\n" +
//                " .limit locals 2 ; Number of parameters + locals\n");
//                " return\n" +
//                " .end method");
//
//        //visit all statements
//        for (int i = 0; i < ctx.statement().size(); i++) {
//            code.addAll(visit(ctx.statement().get(i)));
//        }
//
//        //visit functionDec
//        code.addAll(visit(ctx.functionDec()));

        return code;
    }

    @Override
    public ArrayList<String> visitStartFunctionDec(DomboParser.StartFunctionDecContext ctx) {
        ArrayList<String> code = new ArrayList<>();
        //Add code for main method
        code.add(
        " ; Method definition for public static void main(String[] args)\n" +
                " .method public static main([Ljava/lang/String;)V\n" +
                " .limit stack 2 ; Size of the operand stack\n" +
                " .limit locals 2 ; Number of parameters + locals\n");

        //Add children code
        code.addAll(super.visitStartFunctionDec(ctx));

        //Add code for ending method
        code.add(
                " return\n" +
                " .end method");

        //Return build up code
        return code;
    }

    @Override
    public ArrayList<String> visitReturnVoidCommand(DomboParser.ReturnVoidCommandContext ctx) {
        return super.visitReturnVoidCommand(ctx);
    }

    @Override
    public ArrayList<String> visitStatement(DomboParser.StatementContext ctx) {
        return super.visitStatement(ctx);
    }

    @Override
    public ArrayList<String> visitVarDeclaration(DomboParser.VarDeclarationContext ctx) {

        return super.visitVarDeclaration(ctx);
    }

    @Override
    public ArrayList<String> visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        return super.visitGenericVarDeclaration(ctx);
    }

    @Override
    public ArrayList<String> visitScope(DomboParser.ScopeContext ctx) {
        return super.visitScope(ctx);
    }

    @Override
    public ArrayList<String> visitExpression(DomboParser.ExpressionContext ctx) {
        return super.visitExpression(ctx);
    }

    @Override
    public ArrayList<String> visitVariableAssign(DomboParser.VariableAssignContext ctx) {
        return super.visitVariableAssign(ctx);
    }

    @Override
    public ArrayList<String> visitAddOp(DomboParser.AddOpContext ctx) {
        return super.visitAddOp(ctx);
    }

    @Override
    public ArrayList<String> visitNegateOp(DomboParser.NegateOpContext ctx) {
        return super.visitNegateOp(ctx);
    }

    @Override
    public ArrayList<String> visitMulOp(DomboParser.MulOpContext ctx) {
        return super.visitMulOp(ctx);
    }

    @Override
    public ArrayList<String> visitIntVariable(DomboParser.IntVariableContext ctx) {
        return super.visitIntVariable(ctx);
    }

    @Override
    public ArrayList<String> visitIntValue(DomboParser.IntValueContext ctx) {
        return super.visitIntValue(ctx);
    }

    @Override
    public ArrayList<String> visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        return super.visitCalcComparator(ctx);
    }

    @Override
    public ArrayList<String> visitBoolValue(DomboParser.BoolValueContext ctx) {
        return super.visitBoolValue(ctx);
    }

    @Override
    public ArrayList<String> visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        return super.visitLogicComparator(ctx);
    }

    @Override
    public ArrayList<String> visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        return super.visitBoolVariable(ctx);
    }

    @Override
    public ArrayList<String> visitNotOp(DomboParser.NotOpContext ctx) {
        return super.visitNotOp(ctx);
    }

    @Override
    public ArrayList<String> visitStringAddOp(DomboParser.StringAddOpContext ctx) {
        return super.visitStringAddOp(ctx);
    }

    @Override
    public ArrayList<String> visitStringWithExpression(DomboParser.StringWithExpressionContext ctx) {
        return super.visitStringWithExpression(ctx);
    }

    @Override
    public ArrayList<String> visitStringVariable(DomboParser.StringVariableContext ctx) {
        return super.visitStringVariable(ctx);
    }

    @Override
    public ArrayList<String> visitStringValue(DomboParser.StringValueContext ctx) {
        return super.visitStringValue(ctx);
    }

    @Override
    public ArrayList<String> visitStringReadStatement(DomboParser.StringReadStatementContext ctx) {
        return super.visitStringReadStatement(ctx);
    }

    @Override
    public ArrayList<String> visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        return super.visitIfSingleStatement(ctx);
    }

    @Override
    public ArrayList<String> visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        return super.visitIfElseStatement(ctx);
    }

    @Override
    public ArrayList<String> visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        return super.visitIfElseIfStatement(ctx);
    }

    @Override
    public ArrayList<String> visitWhile(DomboParser.WhileContext ctx) {
        return super.visitWhile(ctx);
    }

    @Override
    public ArrayList<String> visitFor(DomboParser.ForContext ctx) {
        return super.visitFor(ctx);
    }

    @Override
    public ArrayList<String> visitFunctionDeclaration(DomboParser.FunctionDeclarationContext ctx) {
        return super.visitFunctionDeclaration(ctx);
    }

    @Override
    public ArrayList<String> visitFunctionPara(DomboParser.FunctionParaContext ctx) {
        return super.visitFunctionPara(ctx);
    }

    @Override
    public ArrayList<String> visitFunction(DomboParser.FunctionContext ctx) {
        return super.visitFunction(ctx);
    }

    @Override
    public ArrayList<String> visitPrintCommand(DomboParser.PrintCommandContext ctx) {
        return super.visitPrintCommand(ctx);
    }

    @Override
    public ArrayList<String> visitReadCommand(DomboParser.ReadCommandContext ctx) {
        return super.visitReadCommand(ctx);
    }

    @Override
    public ArrayList<String> visitReturnCommand(DomboParser.ReturnCommandContext ctx) {
        return super.visitReturnCommand(ctx);
    }

    @Override
    public ArrayList<String> visitParameter(DomboParser.ParameterContext ctx) {
        return super.visitParameter(ctx);
    }
}