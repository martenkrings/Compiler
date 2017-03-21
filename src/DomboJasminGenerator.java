import Model.*;
import org.antlr.v4.runtime.tree.RuleNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander on 6-3-2017.
 */
public class DomboJasminGenerator extends DomboBaseVisitor<ArrayList<String>> {
    static boolean hasReadStatement = false;
    private Method currentMethod;
    private int lastLabelCreated;

    //List that is fileld by TypeChecker
    public static ArrayList<Variable> classVariables = new ArrayList<>();

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
     * method that find a class variable by its identifier
     *
     * @param identifier identifier of variable to be found
     * @return the found variable
     */
    private Variable findClassVariable(String identifier) {
        //search the classVariable
        for (Variable variable : classVariables) {
            if (variable.getIdentifier().equals(identifier)) {
                return variable;
            }
        }

        //return null, should never happene
        return null;
    }

    /**
     * Calls pushTopOfstackToLocalVariables without a custom position
     *
     * @param localByteCodeParameter parameter to be pushed
     * @param variableIdentifier     identifier of variable
     * @return byte code for storing the variable in local parameters
     */
    private ArrayList<String> pushTopOfStackToStorage(LocalByteCodeParameter localByteCodeParameter, String variableIdentifier) {
        int pos = 0;
        if (currentMethod != null) {
            pos = currentMethod.getLocalVariables().size();
        }

        return pushTopOfStackToStorage(localByteCodeParameter, variableIdentifier, pos);
    }

    /**
     * Generates java byte code for storing a variable in the local parameters
     *
     * @param localByteCodeParameter parameter to be pushed
     * @param variableIdentifier     identifier of variable
     * @param pos                    custom position of variable
     * @return byte code for storing variable in local parameters
     */
    private ArrayList<String> pushTopOfStackToStorage(LocalByteCodeParameter localByteCodeParameter, String variableIdentifier, int pos) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //if variable == null than we're dealing with a class variable
        if (localByteCodeParameter == null) {
            //add code for storing a class variable
            code.add("putfield MyTest/" + variableIdentifier + " " + giveByteCodeMethodType(findClassVariable(variableIdentifier).getDataType()) + "\n");
            return code;
        }

        //add java byteCode depending on dataType
        switch (localByteCodeParameter.getDataType().getType()) {
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
        currentMethod.storeLocalVariable(variableIdentifier, localByteCodeParameter.getDataType());

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
                " .super java/lang/Object ; Inheritance definition\n\n");

        //add code for class variables
        for (Variable variable : classVariables) {
            code.add(".field " + variable.getIdentifier() + " " + giveByteCodeMethodType(variable.getDataType()) + "\n");
        }

        //add start of default constructor
        code.add(
                "\n;Default constructor (empty constructor)\n" +
                        ".method public <init>()V\n");

        //add middle of default constructor, add a stackSize so class variables fit in stack
        code.add(".limit stack 3\n" +
                "aload_0 ; Loads \"this\" on the stack\n" +
                "invokenonvirtual java/lang/Object/<init>()V ; Call super constructor\n");

        //visit global var declarations and add their code
        for (int i = 0; i < ctx.statement().size(); i++) {
            DomboParser.VarDecContext varDecContext = ctx.statement().get(i).varDec();
            if (varDecContext != null) {
                code.addAll(visit(ctx.statement().get(i).varDec()));
            }
        }

        //add end of method
        code.add(
                "return ; Terminate method\n" +
                        ".end method\n\n");


        //visit startFunctionDec
        code.addAll(visit(ctx.startFunctionDec()));

        //visit functions
        for (int i = 0; i < ctx.functionDec().size(); i++) {
            if (ctx.functionDec().get(i) != null) {
                code.addAll(visit(ctx.functionDec(i)));
            }
        }

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitStartFunctionDec(DomboParser.StartFunctionDecContext ctx) {
        //Set current method
        currentMethod = (Method) Dombo.parseTreeProperty.get(ctx);

        //Init arrayList
        ArrayList<String> code = new ArrayList<>();

        //Add code for main method ('secretly starts a run method')
        code.add(
                " ; Model.Method definition for public static void main(String[] args)\n" +
                        ".method public static main([Ljava/lang/String;)V\n" +
                        ".limit stack 5 ; Size of the operand stack\n" +
                        ".limit locals 5 ; Number of parameters + locals\n" +
                        "new MyTest\n" +
                        "dup\n" +
                        "invokenonvirtual MyTest/<init>()V\n" +
                        "invokevirtual MyTest/run()V\n" +
                        " return\n" +
                        ".end method\n\n" +
                        ".method public run()V\n" +
                        ".limit stack 5 ; Size of the operand stack\n" +
                        ".limit locals 5 ; Number of parameters + locals\n");

        //Add children code
        code.addAll(visitChildrenWithoutNull(ctx));

        //Add code for ending method
        code.add(
                " \nreturn\n" +
                        ".end method\n\n");

        //Return
        return code;
    }

    @Override
    public ArrayList<String> visitVarDeclaration(DomboParser.VarDeclarationContext ctx) {
        //init arrayList
        ArrayList<String> code = new ArrayList<>();

        //get the current variable we're visiting
        Variable variable = (Variable) Dombo.parseTreeProperty.get(ctx);

        //change variable to a localByteCodeParameter
        LocalByteCodeParameter dummy;

        //if currentMethod == null than this is a class variable so change dummy to null
        if (currentMethod == null) {
            dummy = null;
            //load this reference
            code.add("aload_0\n");
        } else {
            //'transform' variable to localByteCodeParameter
            dummy = new LocalByteCodeParameter(-1, variable.getDataType());
        }

        //visit children
        code.addAll(visitChildrenWithoutNull(ctx));

        //push top of stack to a new local variable
        code.addAll(pushTopOfStackToStorage(dummy, variable.getIdentifier()));

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitReturnVoidCommand(DomboParser.ReturnVoidCommandContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //add return Void code
        code.add("return\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitStatement(DomboParser.StatementContext ctx) {
        //return result of children
        return visitChildrenWithoutNull(ctx);
    }

    @Override
    public ArrayList<String> visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //return empty list
        return code;
    }

    @Override
    public ArrayList<String> visitBlock(DomboParser.BlockContext ctx) {
        //Visit children
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

        //boolean that 'tells' whether we're visiting a class variable or not
        boolean classVar = true;

        LocalByteCodeParameter currentParameter = null;

        //if currentMethod != null
        if (currentMethod != null) {
            //get the current variable being visited
            currentParameter = currentMethod.getLocalVariable(ctx.name.getText());

            //if currentParameter is found than it is not a class variable
            if (currentParameter != null) {

                classVar = false;
            }
        }

        //if we're dealing with a class variable
        if (classVar) {
            //load this reference
            code.add("aload_0\n");

            //push value to stack
            code.addAll(visit(ctx.expression()));

            //add top of stack to a global variable
            code.addAll(pushTopOfStackToStorage(null, ctx.name.getText()));
            return code;
        }

        //push value to stack
        code.addAll(visit(ctx.expression()));

        //add top of stack to local variables overriding old variable
        code.addAll(pushTopOfStackToStorage(currentParameter, ctx.name.getText(), currentParameter.getPosition()));

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

        //if we're dealing with a class variable
        if (currentMethod == null || currentMethod.getLocalVariable(ctx.ID().getText()) == null) {
            //get this
            code.add("aload_0\n");

            //make class variable code
            code.add("getfield MyTest/" + ctx.ID().getText() + " " + giveByteCodeMethodType(new DataType(DataTypeEnum.INT)) + "\n");
            return code;
        }

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

        //if we're dealing with a class variable
        if (currentMethod == null || currentMethod.getLocalVariable(ctx.ID().getText()) == null) {
            //get this
            code.add("aload_0\n");

            //make class variable code
            code.add("getfield MyTest/" + ctx.ID().getText() + " " + giveByteCodeMethodType(new DataType(DataTypeEnum.BOOLEAN)) + "\n");
            return code;
        }

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
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        code.add("new java/lang/StringBuilder\n" +
                "dup\n" +
                "invokespecial\tjava/lang/StringBuilder/<init>()V ; Call string builder constructor\n");

        //Add StringExpression code
        code.addAll(visit(ctx.left));

        code.add("invokevirtual\tjava/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder; ;String to StringBuilder\n");

        code.addAll(visit(ctx.right));

        code.add("invokevirtual\tjava/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder; ;Append second String to StringBuilder\n" +
                "invokevirtual\tjava/lang/StringBuilder.toString()Ljava/lang/String; ;Call toString from StringBuilder\n" +
                "astore_2\n" +
                "aload_2\n");

        //todo check this load
        return code;
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

        code.add("invokevirtual\tjava/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder; ;String to StringBuilder\n");

        code.addAll(visit(ctx.expression()));

        code.add("invokevirtual\tjava/lang/StringBuilder.append(I)Ljava/lang/StringBuilder; ;Append number to StringBuilder\n" +
                "invokevirtual\tjava/lang/StringBuilder.toString()Ljava/lang/String; ;Call toString from StringBuilder\n" +
                "astore_2\n" +
                "aload_2\n");

        return code;
    }

    @Override
    public ArrayList<String> visitStringVariable(DomboParser.StringVariableContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //if we're dealing with a class variable
        if (currentMethod == null || currentMethod.getLocalVariable(ctx.ID().getText()) == null) {
            //get this
            code.add("aload_0\n");

            //make class variable code
            code.add("getfield MyTest/" + ctx.ID().getText() + " " + giveByteCodeMethodType(new DataType(DataTypeEnum.STRING)) + "\n");
            return code;
        }

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
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //get current method
        Method method = (Method) Dombo.parseTreeProperty.get(ctx);

        //change current method and set its parent method
        Method temp = currentMethod;
        currentMethod = method;
        currentMethod.setParentMethod(temp);

        //get list of the methods parameters
        List list = method.getMethodType().getParameters();

        //Add begin of method code
        code.add(".method public " + method.getIdentifier() + "(");

        //for each parameter of the method
        for (int i = 0; i < list.size(); i++) {
            code.add(giveByteCodeMethodType((DataType) list.get(i)));
        }

        //add returnType
        code.add(")" + giveByteCodeMethodType(method.getMethodType().getReturnType()) + "\n");

        //add stack and local size
        code.add(".limit stack 5\n.limit locals 5\n");

        //visit children
        code.addAll(visitChildrenWithoutNull(ctx));

        //add end of method code
        code.add(".end method\n");

        //change current method, a normal method will always have a parent method
        currentMethod = currentMethod.getParentMethod();

        //return
        return code;
    }

    /**
     * Gives bytecode method type for DataTypes
     * Integer => I
     * Boolean => I
     * String => Ljava/lang/String
     * Void => V
     *
     * @param dataType datatype for wich to write bytecode
     * @return bytecode datatype
     */
    public String giveByteCodeMethodType(DataType dataType) {
        switch (dataType.getType()) {
            case "STRING":
                return "Ljava/lang/String;";
            case "BOOLEAN":
                return "I";
            case "INT":
                return "I";
            case "VOID":
                return "V";

            default:
                return "SHOULD NOT REACH THIS CODE, giveByteCodeMethodType";
        }
    }

    @Override
    public ArrayList<String> visitFunctionPara(DomboParser.FunctionParaContext ctx) {
        return super.visitFunctionPara(ctx);
    }

    @Override
    public ArrayList<String> visitFunction(DomboParser.FunctionContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //Get the method we're calling
        Method method = (Method) Dombo.parseTreeProperty.get(ctx);

        //TODO ask this

        //load this reference
        code.add("aload_0 ;load this reference\n");

        //add start code of function call
        code.add("invokevirtual MyTest/" + method.getIdentifier() + "(");

        //get parameter List
        List methodParameters = method.getMethodType().getParameters();

        //add parameters code
        for (int i = 0; i < methodParameters.size(); i++) {
            code.add(giveByteCodeMethodType((DataType) methodParameters.get(i)));
        }

        //add returnType code
        code.add(")" + giveByteCodeMethodType(method.getMethodType().getReturnType()) + "\n");

        //return
        return code;
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
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //new Scanner
        code.add("new java/util/Scanner\ndup\n");

        //get input stream
        code.add("getstatic\tjava/lang/System.in Ljava/io/InputStream;\n");

        //call constructor
        code.add("invokespecial\tjava/util/Scanner/<init>(Ljava/io/InputStream;)V\n");

        //get nextLine
        code.add("invokevirtual\tjava/util/Scanner/nextLine()Ljava/lang/String;\n");

        //return
        return code;
    }

    @Override
    public ArrayList<String> visitReturnCommand(DomboParser.ReturnCommandContext ctx) {
        //init ArrayList
        ArrayList<String> code = new ArrayList<>();

        //visit returned expression
        code.addAll(visitChildrenWithoutNull(ctx));


        //add code depending on returnType
        switch (currentMethod.getMethodType().getReturnType().getType()) {
            case "STRING":
                code.add("areturn\n");
                break;
            case "BOOLEAN":
                code.add("ireturn\n");
                break;
            case "INT":
                code.add("ireturn\n");
                break;

            default:
                code.add("SHOULD NOT REACH THIS CODE, visitReturnCommand\n");
        }

        //return
        return code;
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