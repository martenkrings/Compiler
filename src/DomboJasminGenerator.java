import Model.DataType;
import Model.LocalByteCodeParameter;
import Model.Method;
import Model.Variable;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.ArrayList;

/**
 * Created by Sander on 6-3-2017.
 */
public class DomboJasminGenerator extends DomboBaseVisitor<ArrayList<String>> {
    private Method currentMethod;
    private int lastLabelCreated;

    private ArrayList<String> visitChildrenWithoutNull(RuleNode ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //visit all children
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ArrayList<String> temp = visit(ctx.getChild(i));
            //add children results to code if they aren't null
            if (temp != null) {
                code.addAll(temp);
            }
        }

        //return
        return code;
    }

    /**
     * Calls pushTopOfstackToLocalVariables without a custom position
     *
     * @param dataType           dataType of variable
     * @param variableIdentifier identifier of variable
     * @return byte code for storing the variable in local parameters
     */
    private ArrayList<String> pushTopOfStackToLocalVariables(DataType dataType, String variableIdentifier) {
        return pushTopOfStackToLocalVariables(dataType, variableIdentifier, currentMethod.getLocalVariables().size());
    }

    /**
     * Generates java byte code for storing a variable in the local parameters
     *
     * @param dataType           dataType of variable
     * @param variableIdentifier identifier of variable
     * @param pos                custom position of variable
     * @return byte code for storing variable in local parameters
     */
    private ArrayList<String> pushTopOfStackToLocalVariables(DataType dataType, String variableIdentifier, int pos) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add java byteCode depending on dataType
        switch (dataType.getType()) {
            case "BOOLEAN":
                code.add("istore_" + pos + "\n");
                break;
            case "STRING":
                code.add("astore_" + pos + "\n");
                break;
            case "INT":
                code.add("istore_" + pos + "\n");
                break;
            default:
                //We should not get here
                code.add("CRASH ON THIS, PushTopOfStack\n");
                break;
        }

        //remember where we stored the new local variable
        currentMethod.storeLocalVariable(variableIdentifier, dataType);

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitProgram(DomboParser.ProgramContext ctx) {
        //init lastLabelCreated
        lastLabelCreated = -1;

        //init ArrayList
        ArrayList<String> code = new ArrayList<>();


        //Add code for the class
        code.add(".class public MyTest ; Name and access modifier of the class\n" +
                " .super java/lang/Object ; Inheritance definition\n" +
                "\n" +
                " ; Default constructor (empty constructor)\n" +
                " .method public <init>()V\n" +
                " aload_0 ; Loads \"this\" on the stack\n" +
                " invokenonvirtual java/lang/Object/<init>()V ; Call super constructor\n" +
                " return ; Terminate method\n" +
                " .end method\n\n");


        //visit children
        code.addAll(visitChildrenWithoutNull(ctx));

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitStartFunctionDec(DomboParser.StartFunctionDecContext ctx) {
        //Set current method
        currentMethod = (Method) Dombo.parseTreeProperty.get(ctx);
        //Init arrayList
        ArrayList<String> code = new ArrayList<>();

        //Add code for main method
        code.add(
                " ; Model.Method definition for public static void main(String[] args)\n" +
                        " .method public static main([Ljava/lang/String;)V\n" +
                        " .limit stack 100 ; Size of the operand stack\n" +
                        " .limit locals 100 ; Number of parameters + locals\n\n");

        //Add children code
        code.addAll(visitChildrenWithoutNull(ctx));

        //Add code for ending method
        code.add(
                " \n\nreturn\n" +
                        ".end method");

        //Returnr
        return code;
    }

    @Override
    public ArrayList<String> visitVarDeclaration(DomboParser.VarDeclarationContext ctx) {
        //init arrayList
        ArrayList<String> code = new ArrayList<>();

        //visit children
        code.addAll(visitChildrenWithoutNull(ctx));

        //get the current variable we're visiting
        Variable variable = (Variable) Dombo.parseTreeProperty.get(ctx);

        //push top of stack to a new local variable
        code.addAll(pushTopOfStackToLocalVariables(variable.getDataType(), variable.getIdentifier()));

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitReturnVoidCommand(DomboParser.ReturnVoidCommandContext ctx) {
        ArrayList<String> code = new ArrayList<>();
        code.add("this is just a stupid test in the void return command");
        //return empty list
        return code;
    }

    @Override
    public ArrayList<String> visitStatement(DomboParser.StatementContext ctx) {
        //return result of children
        return visitChildrenWithoutNull(ctx);
    }

    @Override
    public ArrayList<String> visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        //Generic var declaration has no java bytecode code
        //Add nothing to code
        return null;
    }

    @Override
    public ArrayList<String> visitBlock(DomboParser.BlockContext ctx) {
        //todo
        return visitChildrenWithoutNull(ctx);
    }

    @Override
    public ArrayList<String> visitExpression(DomboParser.ExpressionContext ctx) {
        //return result of children
        return visitChildrenWithoutNull(ctx);
    }

    @Override
    public ArrayList<String> visitVariableAssign(DomboParser.VariableAssignContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get the current variable being visited
        LocalByteCodeParameter currentParameter = currentMethod.getLocalVariable(ctx.name.getText());

        //push value to stack
        code.addAll(visit(ctx.expression()));

        //add top of stack to local variables overriding old variable
        code.addAll(pushTopOfStackToLocalVariables(currentParameter.getDataType(), ctx.name.getText(), currentParameter.getPosition()));

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitAddOp(DomboParser.AddOpContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get lefts code
        code.addAll(visit(ctx.left));

        //get rights code
        code.addAll(visit(ctx.right));

        //add code for used operator
        switch (ctx.op.getText()) {
            case "+":
                code.add("iadd\n");
                break;
            case "-":
                code.add("isub\n");
                break;

            //We should never come here
            default:
                code.add("BREAK ON THIS, visitAddOp\n");
                break;
        }

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitNegateOp(DomboParser.NegateOpContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Add code from child
        code.addAll(visit(ctx.calcExpression()));

        //Add -1 to stack
        code.add("lcd -1\n");

        //multiply
        code.add("imul\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitMulOp(DomboParser.MulOpContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add lefts code
        code.addAll(visit(ctx.left));

        //add rights code
        code.addAll(visit(ctx.right));

        //add code for used operator
        switch (ctx.op.getText()) {
            case "*":
                code.add("imul\n");
                break;
            case "/":
                code.add("idiv\n");
                break;
            case "%":
                code.add("irem\n");
                break;

            //We should never come here
            default:
                code.add("BREAK ON THIS, visitMulOp\n");
                break;
        }

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitIntVariable(DomboParser.IntVariableContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Load localParameter(int)
        code.add("iload_" + currentMethod.getLocalVariable(ctx.ID().getText()).getPosition() + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitIntValue(DomboParser.IntValueContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add push to stack code
        code.add("ldc " + ctx.getText() + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Add code for left
        code.addAll(visit(ctx.leftCalc));

        //Add code for right
        code.addAll(visit(ctx.rightCalc));

        //Add compare code for used operator
        code.add("if_icmp");
        switch (ctx.op.getText()) {
            case "<":
                code.add("lt");
                break;
            case ">":
                code.add("gt");
                break;
            case ">=":
                code.add("ge");
                break;
            case "<=":
                code.add("le");
                break;
            case "==":
                code.add("eq");
                break;
            case "!=":
                code.add("ne");
                break;

            //we should never come here
            default:
                code.add("CRASH ON THIS, visitCalcComparator");
                break;
        }

        //make rest of code in method
        code.addAll(makeStandardComparatorCode());

        return code;
    }

    @Override
    public ArrayList<String> visitBoolValue(DomboParser.BoolValueContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add push to stack code, true == 1 false == 0
        if (ctx.getText().equalsIgnoreCase("true")) {
            code.add("ldc " + 1 + "\n");
            return code;
        }
        code.add("ldc " + 0 + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Add code for left
        code.addAll(visit(ctx.leftLogic));

        //Add code for right
        code.addAll(visit(ctx.rightLogic));

        //Add compare code for used operator
        code.add("if_icmp");
        switch (ctx.op.getText()) {
            case "==":
                code.add("eq");
                break;
            case "!=":
                code.add("ne");
                break;

            //we should never come here
            default:
                code.add("CRASH ON THis, visitLogicComparator");
                break;
        }

        //make rest of code
        code.addAll(makeStandardComparatorCode());

        return code;
    }

    public ArrayList<String> makeStandardComparatorCode() {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //increase lastLabelCreated for a new label
        lastLabelCreated++;

        //store trueLabel
        int trueLabel = lastLabelCreated;

        //add where the code should jump to if true
        code.add(" label" + trueLabel + "\n");

        //Code for if false
        code.add("ldc 0\n");

        //increase lastLabelCreated for a new label marking end of if statement
        lastLabelCreated++;

        //store endOfIf label
        int endOfIfLabel = lastLabelCreated;

        code.add("goto label" + endOfIfLabel + "\n");

        //Add true label
        code.add("label" + trueLabel + ":\n");

        //When true
        code.add("ldc 1\n");

        //Add end of if label
        code.add("label" + endOfIfLabel + ":\n");

        return code;
    }

    @Override
    public ArrayList<String> visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Load localParameter(boolean(int))
        code.add("iload_" + currentMethod.getLocalVariable(ctx.ID().getText()).getPosition() + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitNotOp(DomboParser.NotOpContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add code of logicExpression
        code.addAll(visit(ctx.logicExpression()));

        //Add code for true
        code.add("ldc 1\n");

        //generate new label
        lastLabelCreated++;

        //add code for not equals if
        code.add("if_icmpne label" + lastLabelCreated + "\n");

        //add code for if false
        code.add("ldc 0\n");

        //generate new label
        lastLabelCreated++;

        //jump to end of if statement
        code.add("goto label" + lastLabelCreated + "\n");

        int prevLabel = lastLabelCreated - 1;

        //code for if true
        code.add("label" + prevLabel + ":\n");

        //add code for true
        code.add("ldc 1\n");

        //add code that marks end of if
        code.add("label" + lastLabelCreated + ":\n");

        return code;
    }

    @Override
    public ArrayList<String> visitStringAddOp(DomboParser.StringAddOpContext ctx) {
        return super.visitStringAddOp(ctx);
    }

    @Override
    public ArrayList<String> visitStringWithExpression(DomboParser.StringWithExpressionContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        code.add("new java/lang/StringBuilder\n" +
                "dup\n" +
                "invokespecial\tjava/lang/StringBuilder/<init>()V ; Call string builder constructor\n");

        //Add StringExpression code
        code.addAll(visit(ctx.stringExpression()));

        code.add("invokevirtual\tjava/lang/StringBuilder.append(Ljava/lang/String;)V ;String number to StringBuilder\n");

        code.addAll(visit(ctx.expression()));

        code.add("invokevirtual\tjava/lang/StringBuilder.append(I)V ;Append number to StringBuilder\n" +
                "invokevirtual\tjava/lang/StringBuilder.toString()V ;Call toString from StringBuilder\n" +
                "astore_2\n" +
                "aload_2\n");

        return code;
    }

    @Override
    public ArrayList<String> visitStringVariable(DomboParser.StringVariableContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Load localParameter(Object)
        code.add("aload_" + currentMethod.getLocalVariable(ctx.ID().getText()).getPosition() + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitStringValue(DomboParser.StringValueContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add java byte code Object(Sting) init code
        code.add("ldc " + ctx.getText() + "\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitStringReadStatement(DomboParser.StringReadStatementContext ctx) {
        return super.visitStringReadStatement(ctx);
    }

    @Override
    public ArrayList<String> visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get condition
        code.addAll(visit(ctx.condition));

        //add true boolean
        code.add("ldc 1\n");

        //increase lastLabelCreated for a new label
        lastLabelCreated++;

        //store label marking true
        int trueLabel = lastLabelCreated;

        //add if statement
        code.add("if_icmpeq label" + trueLabel + "\n");

        //increase lastLabelCreated for a new label marking end of if statement
        lastLabelCreated++;

        //store lable marking end of if
        int endLabel = lastLabelCreated;

        //jump to end of if
        code.add("goto label" + endLabel + "\n");

        //Add true label
        code.add("label" + trueLabel + ":\n");

        //visit true block
        code.addAll(visit(ctx.block()));

        //Add end of if label
        code.add("label" + endLabel + ":\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get condition
        code.addAll(visit(ctx.condition));

        //add true boolean
        code.add("ldc 1\n");

        //increase lastLabelCreated for a new label
        lastLabelCreated++;

        //remember label marking true
        int trueLabel = lastLabelCreated;

        //add if statement
        code.add("if_icmpeq label" + trueLabel + "\n");

        //code for if false(visit the second block)
        code.addAll(visit(ctx.block().get(1)));

        //increase lastLabelCreated for a new label marking end of if statement
        lastLabelCreated++;

        //StoreEndOfIfLabel
        int endOfIfLabel = lastLabelCreated;

        //jump to the end of if statement
        code.add("goto label" + endOfIfLabel + "\n");

        //Add true label
        code.add("label" + trueLabel + ":\n");

        //visit true block
        code.addAll(visit(ctx.block().get(0)));

        //Add end of if label
        code.add("label" + endOfIfLabel + ":\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get condition
        code.addAll(visit(ctx.condition));

        //add true boolean
        code.add("ldc 1\n");

        //increase lastLabelCreated for a new label
        lastLabelCreated++;

        //get the previous label
        int trueLabel = lastLabelCreated;

        //add if statement
        code.add("if_icmpeq label" + trueLabel + "\n");

        //code for if false(visit the other if statement)
        code.addAll(visit(ctx.ifStatement()));

        //increase lastLabelCreated for a new label marking end of if statement
        lastLabelCreated++;

        //StoreEndOfIfLabel
        int endOfIfLabel = lastLabelCreated;

        //jump to the end of if statement
        code.add("goto label" + endOfIfLabel + "\n");

        //Add true label
        code.add("label" + trueLabel + ":\n");

        //visit true block
        code.addAll(visit(ctx.block()));

        //Add end of if label
        code.add("label" + endOfIfLabel + ":\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitWhile(DomboParser.WhileContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //generate new label
        lastLabelCreated++;

        //store beginOfWhileLabel
        int beginOfWhileLabel = lastLabelCreated;

        //Set beginOfWhileLabel
        code.add("label" + beginOfWhileLabel + ":\n");

        //Add condition code
        code.addAll(visit(ctx.condition));

        //Add true code
        code.add("ldc 1\n");

        //generate new label
        lastLabelCreated++;

        //store endOfWhileLabel
        int endOfWhileLabel = lastLabelCreated;

        //generate new label
        lastLabelCreated++;

        //store ifCondition label
        int conditionLabel = lastLabelCreated;

        //Ádd if code
        code.add("if_icmpeq label" + conditionLabel + "\n");

        //If condition broken skip to end of While loop
        code.add("goto label" + endOfWhileLabel + "\n");

        //set condition label
        code.add("label" + conditionLabel + ":\n");

        //if false than visit block
        code.addAll(visit(ctx.block()));

        //jump back to top
        code.add("goto label" + beginOfWhileLabel + "\n");

        //set endOfWhileLabel
        code.add("label" + endOfWhileLabel + ":\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitFor(DomboParser.ForContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //code for forLoop counter var declaration
        code.addAll(visit(ctx.vardec));

        //generate new label
        lastLabelCreated++;

        //store beginOfWhileLabel
        int beginOfForLabel = lastLabelCreated;

        //Set beginOfWhileLabel
        code.add("label" + beginOfForLabel + ":\n");

        //Add condition code
        code.addAll(visit(ctx.condition));

        //Add true code
        code.add("ldc 1\n");

        //generate new label
        lastLabelCreated++;

        //store endOfWhileLabel
        int endOfForLabel = lastLabelCreated;

        //generate new label
        lastLabelCreated++;

        //store ifCondition label
        int conditionLabel = lastLabelCreated;

        //Ádd if code
        code.add("if_icmpeq label" + conditionLabel + "\n");

        //If condition broken skip to end of While loop
        code.add("goto label" + endOfForLabel + "\n");

        //set condition label
        code.add("label" + conditionLabel + ":\n");

        //if false than visit block
        code.addAll(visit(ctx.block()));

        //add code that executes forLoops given command
        code.addAll(visit(ctx.variableAssign()));

        //jump back to top
        code.add("goto label" + beginOfForLabel + "\n");

        //set endOfWhileLabel
        code.add("label" + endOfForLabel + ":\n");

        //return
        return code;
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
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Get System.out code
        code.add("getstatic\t\tjava/lang/System.out Ljava/io/PrintStream;\n");

        //push the to print value to the stack
        code.addAll(visit(ctx.stringExpression()));

        //print the value
        code.add("invokevirtual\tjava/io/PrintStream.println(Ljava/lang/String;)V\n");

        //return
        return code;
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
    public ArrayList<String> visitStringParameter(DomboParser.StringParameterContext ctx) {
        return super.visitStringParameter(ctx);
    }

    @Override
    public ArrayList<String> visitCalcParameter(DomboParser.CalcParameterContext ctx) {
        return super.visitCalcParameter(ctx);
    }

    @Override
    public ArrayList<String> visitLogicParameter(DomboParser.LogicParameterContext ctx) {
        return super.visitLogicParameter(ctx);
    }
}