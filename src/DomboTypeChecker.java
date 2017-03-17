
import Model.*;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Marten on 2/28/2017.
 */
public class DomboTypeChecker extends DomboBaseVisitor<DataType> {
    private Stack<Scope> scopes;
    private boolean definingFunction = true;

    /**
     * Finds a variable or method accessible from current scope
     *
     * @param ctxId name of variable
     * @return Model.Type if found else null
     */
    public Type lookUpVariableInScopes(String ctxId) {
        Scope searchingScope = scopes.peek();
        Symbol foundSymbol;

        //While we have scopes to search search
        while (searchingScope != null) {
            //search scope for the symbol
            foundSymbol = searchingScope.lookUpVariable(ctxId);

            //if no symbol is found try the next parent scope
            if (foundSymbol == null) {
                searchingScope = searchingScope.getParentScope();
            } else {
                return foundSymbol.type;
            }
        }

        return null;
    }

    /**
     * Compares 2 dataTypes
     * @param d1 first dataType to compare
     * @param d2 second dataType(String) to compare
     * @return true if of same type else false
     */
    public boolean compareDataTypes(DataType d1, String d2) {
        return d1.getType().equalsIgnoreCase(d2);
    }

    @Override
    public DataType visitProgram(DomboParser.ProgramContext ctx) {
        //start up
        scopes = new Stack<>();
        scopes.add(new Scope());

        //visit functions
        for (int i = 0; i < ctx.functionDec().size(); i++) {
            if (ctx.functionDec().get(i) != null) {
                visit(ctx.functionDec(i));
            }
        }

        //we are no longer defining functions
        definingFunction = false;

        //visit global var declarations
        for (int i = 0; i < ctx.statement().size(); i++){
            DomboParser.VarDecContext varDecContext = ctx.statement().get(i).varDec();
            if (varDecContext != null) {
                visit(ctx.statement().get(i).varDec());
            }
        }

        //visit the START function
        visit(ctx.startFunctionDec());

        //Visit the rest, note it does not matter that above functions get visited twice, they will simply be overridden
        super.visitProgram(ctx);

        //return 'something'
        return new DataType("");
    }

    @Override
    public DataType visitStartFunctionDec(DomboParser.StartFunctionDecContext ctx) {
        //Declare new method
        scopes.peek().declareMethod("Main", new DataType(DataTypeEnum.VOID), new ArrayList<>());

        //Add to parseTreeProperty
        Dombo.parseTreeProperty.put(ctx, new Method("Main", new MethodType(new DataType(DataTypeEnum.VOID), new ArrayList<>())));

        //add scope
        scopes.add(new Scope(scopes.peek(), new DataType(DataTypeEnum.VOID)));

        //visit children
        DataType dataType =  super.visitStartFunctionDec(ctx);

        //remove scope
        scopes.pop();

        //return datatype
        return dataType;
    }

    @Override
    public DataType visitVariableAssign(DomboParser.VariableAssignContext ctx) {
        //Look up DataTypes
        Type variableType = lookUpVariableInScopes(ctx.name.getText());
        DataType valueDataType = visit(ctx.value);

        //If varaible not found throw new Model.TypeError
        if (variableType == null | !(variableType instanceof DataType)) {
            try {
                throw new TypeError(ctx.name.getText() + " not initialised. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //Cast variableType
        DataType variableDataType = (DataType) variableType;

        //Compare types, if types do not compare throw new Model.TypeError
        if (!compareDataTypes(variableDataType, valueDataType.getType())) {
            try {
                throw new TypeError("Expected: " + variableDataType.getType() + " got " + valueDataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //Return variableDataType
        return variableDataType;
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

        //var name is not allowed to be RETURN
        if (ctx.ID().getText().equals("RETURN")){
            try {
                throw new TypeError("Model.Variable name can not be RETURN");
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //Compare declared and actual dataType
        if (actualDataType != null) {
            if (!compareDataTypes(actualDataType, datatype)) {
                try {
                    throw new TypeError(datatype + " and " + actualDataType.getType() + " type mismatch. At line: " + ctx.start.getLine());
                } catch (TypeError typeError) {
                    typeError.printStackTrace();
                }
            }
        }

        //Add variable
        scopes.peek().declareVariable(ctx.ID().getText(), actualDataType);

        //Add variable to parseTreeProperty
        Dombo.parseTreeProperty.put(ctx, new Variable(ctx.ID().getText(), actualDataType));

        super.visitVarDeclaration(ctx);

        //return Model.DataType
        return actualDataType;
    }

    @Override
    public DataType visitGenericVarDeclaration(DomboParser.GenericVarDeclarationContext ctx) {
        //Add a new variable to the current scope
        scopes.peek().declareVariable(ctx.ID().getText(), new DataType(ctx.DATATYPE().getText()));

        //Add variable to parseTreeProperty
        Dombo.parseTreeProperty.put(ctx, new Variable(ctx.ID().getText(), new DataType(ctx.DATATYPE().getText())));

        //Visit children
        return super.visitGenericVarDeclaration(ctx);
    }

    @Override
    public DataType visitBlock(DomboParser.BlockContext ctx) {
        //Visit children
        super.visitBlock(ctx);

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

        //Check if mul operator is being applied to a boolean if so throw new Model.TypeError
        if (compareDataTypes(leftDataType, DataTypeEnum.BOOLEAN.toString()) | compareDataTypes(rightDataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError(DataTypeEnum.BOOLEAN.toString() + " not compatible with AddOp. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //compare types
        if (!compareDataTypes(leftDataType, rightDataType.getType())) {
            //throw error if types mismatch
            try {
                throw new TypeError(leftDataType.getType() + " and " + rightDataType.getType() + " type mismatch. At line: " + ctx.start.getLine());
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
                throw new TypeError("negate op only usable on INT variables. At line: " + ctx.start.getLine());
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

        //Check if mul operator is being applied to a boolean if so throw new Model.TypeError
        if (compareDataTypes(leftDataType, DataTypeEnum.BOOLEAN.toString()) | compareDataTypes(rightDataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError(DataTypeEnum.BOOLEAN.toString() + " not compatible with MulOperator. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //compare types
        if (!compareDataTypes(leftDataType, rightDataType.getType())) {
            //throw error if types missmatch
            try {
                throw new TypeError(leftDataType.getType() + " and " + rightDataType.getType() + " type mismatch. At line: " + ctx.start.getLine());
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
                throw new TypeError(ctx.ID().getText() + " not initialised. At line: " + ctx.start.getLine());
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
        //return Model.DataType
        return new DataType(DataTypeEnum.INT);
    }

    @Override
    public DataType visitCalcComparator(DomboParser.CalcComparatorContext ctx) {
        //Get left and right DataTypes
        DataType leftDataType = visit(ctx.leftCalc);
        DataType rightDataType = visit(ctx.rightCalc);

        //If dataType's are not INT throw a new Model.TypeError
        if (!compareDataTypes(leftDataType, DataTypeEnum.INT.toString()) || !compareDataTypes(rightDataType, DataTypeEnum.INT.toString())) {
            try {
                throw new TypeError("CaclComperator can not be used on " + leftDataType.getType() + " and " + rightDataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        super.visitCalcComparator(ctx);

        //return boolean Model.DataType
        return new DataType(DataTypeEnum.BOOLEAN);
    }

    @Override
    public DataType visitBoolValue(DomboParser.BoolValueContext ctx) {
        //return Model.DataType
        return new DataType(DataTypeEnum.BOOLEAN);
    }

    @Override
    public DataType visitLogicComparator(DomboParser.LogicComparatorContext ctx) {
        //Get left and right DataTypes
        DataType leftDataType = visit(ctx.leftLogic);
        DataType rightDataType = visit(ctx.rightLogic);

        //If dataType's are not INT throw a new Model.TypeError
        if (!compareDataTypes(leftDataType, DataTypeEnum.BOOLEAN.toString()) || !compareDataTypes(rightDataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("LogicComparator can not be used on " + leftDataType.getType() + " and " + rightDataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        super.visitLogicComparator(ctx);

        //return new Model.DataType
        return leftDataType;
    }

    @Override
    public DataType visitBoolVariable(DomboParser.BoolVariableContext ctx) {
        DataType found = (DataType) lookUpVariableInScopes(ctx.ID().getText());
        if (found == null) {
            try {
                throw new TypeError(ctx.ID().getText() + " not initialised. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //return the type of the found variable
        return found;
    }

    @Override
    public DataType visitNotOp(DomboParser.NotOpContext ctx) {
        //visit children
        super.visitNotOp(ctx);

        //return boolean DataType
        return new DataType(DataTypeEnum.BOOLEAN);
    }

    @Override
    public DataType visitIfSingleStatement(DomboParser.IfSingleStatementContext ctx) {
        scopes.add(new Scope(scopes.peek()));

        //Get condition Model.DataType
        DataType dataType = visit(ctx.condition);

        //Check if conditionDataType is of boolean type if not throw new Model.TypeError
        if (!compareDataTypes(dataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("Expected " + DataTypeEnum.BOOLEAN + " for if condition got " + dataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //visit children
        super.visitIfSingleStatement(ctx);

        //remove Model.Scope
        scopes.pop();

        return null;
    }

    @Override
    public DataType visitIfElseStatement(DomboParser.IfElseStatementContext ctx) {
        //add new Model.Scope
        scopes.add(new Scope(scopes.peek()));

        //Get condition Model.DataType
        DataType dataType = visit(ctx.condition);

        //Check if conditionDataType is of boolean type is not throw new Model.TypeError
        if (!compareDataTypes(dataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("Expected " + DataTypeEnum.BOOLEAN + " for if condition got " + dataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //visit the first block
        visit(ctx.block(0));

        //remove Model.Scope
        scopes.pop();

        //add new Model.Scope
        scopes.add(new Scope(scopes.peek()));

        //visit the second block
        visit(ctx.block(1));

        //remove Model.Scope
        scopes.pop();

        //return 'something'
        return null;
    }

    @Override
    public DataType visitIfElseIfStatement(DomboParser.IfElseIfStatementContext ctx) {
        //add new Model.Scope
        scopes.add(new Scope(scopes.peek()));

        //Get condition Model.DataType
        DataType dataType = visit(ctx.condition);

        //Check if conditionDataType is of boolean type is not throw new Model.TypeError
        if (!compareDataTypes(dataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("Expected " + DataTypeEnum.BOOLEAN + " for if condition got " + dataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //visit the block first
        visit(ctx.block());

        //remove Model.Scope
        scopes.pop();

        //visit new if statement(that will make its own new Model.Scope)
        visit(ctx.ifStatement());

        //return something
        return null;
    }

    @Override
    public DataType visitWhile(DomboParser.WhileContext ctx) {
        //add new Model.Scope
        scopes.add(new Scope(scopes.peek()));

        //Get condition Model.DataType
        DataType dataType = visit(ctx.condition);

        //Check if conditionDataType is of boolean type is not throw new Model.TypeError
        if (!compareDataTypes(dataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("Expected " + DataTypeEnum.BOOLEAN + " for while condition got " + dataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //visit children
        super.visitWhile(ctx);

        //remove Model.Scope
        scopes.pop();

        //return 'something'
        return null;
    }

    @Override
    public DataType visitFor(DomboParser.ForContext ctx) {
        //declare new Model.Scope
        scopes.add(new Scope(scopes.peek()));

        //Get dataTypes
        DataType varDecDataType = visit(ctx.vardec);
        DataType conditionDataType = visit(ctx.condition);

        //check if first parameter is of type INT
        if (!compareDataTypes(varDecDataType, DataTypeEnum.INT.toString())) {
            try {
                throw new TypeError("First parameter in for loop should declare an INT got " + varDecDataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //check if second parameter is of type BOOLEAN
        if (!compareDataTypes(conditionDataType, DataTypeEnum.BOOLEAN.toString())) {
            try {
                throw new TypeError("Second parameter in for loop should be a BOOLEAN got " + conditionDataType.getType() + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        super.visitFor(ctx);

        //remove Model.Scope
        scopes.pop();

        //return 'something'
        return null;
    }

    @Override
    public DataType visitFunctionDeclaration(DomboParser.FunctionDeclarationContext ctx) {
        //If we're defining function in the scope that add this method to the scope
        if (definingFunction){
            //Make list to store parameters
            ArrayList<DataType> dataTypes = new ArrayList();

            //add dataTypes of parameters
            for (int i = 0; i < ctx.functionParameter().size(); i++) {
                dataTypes.add(visit(ctx.functionParameter().get(i)));
            }

            //declare new method in scope
            scopes.peek().declareMethod(ctx.name.getText(), new DataType(ctx.returntype.getText()), dataTypes);

            //Add new method to parseTreeProperty
            Dombo.parseTreeProperty.put(ctx, new Method(ctx.name.getText(), new MethodType(new DataType(ctx.returntype.getText()), dataTypes)));

            //else visit all children in a new scope
        }else {

            //Make new Model.Scope
            scopes.add(new Scope(scopes.peek(), new DataType(ctx.returntype.getText())));

            //Make list to store parameters
            ArrayList<DataType> dataTypes = new ArrayList();

            //add dataTypes of parameters
            for (int i = 0; i < ctx.functionParameter().size(); i++) {
                dataTypes.add(visit(ctx.functionParameter().get(i)));
            }

            //visit children
            for (int i = 0; i < ctx.statement().size(); i++) {
                visit(ctx.statement(i));
            }
            visit(ctx.returnStatement());

            //remove Model.Scope
            scopes.pop();
        }

        //return 'something'
        return null;
    }

    @Override
    public DataType visitFunctionPara(DomboParser.FunctionParaContext ctx) {
        //get dataType
        DataType dataType = new DataType(ctx.dataType.getText());

        //don't declare a new parameter in current scope if we're only defining the method
        if (!definingFunction) {
            //declare parameter
            scopes.peek().declareVariable(ctx.name.getText(), new DataType(ctx.dataType.getText()));
        }

        //return dataType
        return dataType;
    }

    @Override
    public DataType visitFunction(DomboParser.FunctionContext ctx) {
        //loop up function
        Type type = lookUpVariableInScopes(ctx.name.getText());

        //If method is not defined or not a function throw new typeError
        if (type == null | !(type instanceof MethodType)) {
            try {
                throw new TypeError("Model.Method " + ctx.name.getText() + " not defined. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //Cast type to methodType
        MethodType methodType = (MethodType) type;

        //Collect parameter DataTypes
        ArrayList<DataType> dataTypes = new ArrayList<>();
        for (int i = 0; i < ctx.parameter().size(); i++) {
            dataTypes.add(visit(ctx.parameter().get(i)));
        }

        boolean goodInput = true;

        //check if datTypes given match required parameters of method
        if (dataTypes.size() != methodType.getParameters().size()) {
            goodInput = false;
        } else {
            for (int i = 0; i < methodType.getParameters().size(); i++) {
                if (!methodType.getParameters().get(i).getType().equalsIgnoreCase(dataTypes.get(i).getType())) {
                    goodInput = false;
                }
            }
        }

        //If parameter types don't match throw a Model.TypeError
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
                for (int i = 0; i < dataTypes.size(); i++) {
                    got += dataTypes.get(i).getType();
                    if (dataTypes.size() - 1 != i) {
                        got += ", ";
                    }
                }

                got += ")";

                //Throw new error
                throw new TypeError("Expected: " + expected + " got " + got + ". At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //return return Model.DataType
        return methodType.getReturnType();
    }

    @Override
    public DataType visitStringParameter(DomboParser.StringParameterContext ctx) {
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitCalcParameter(DomboParser.CalcParameterContext ctx) {
        //visit children
        return super.visitCalcParameter(ctx);
    }

    @Override
    public DataType visitLogicParameter(DomboParser.LogicParameterContext ctx) {
        //visit children
        return super.visitLogicParameter(ctx);
    }

    @Override
    public DataType visitPrintCommand(DomboParser.PrintCommandContext ctx) {
        //Do nothing
        return super.visitPrintCommand(ctx);
    }

    @Override
    public DataType visitStringAddOp(DomboParser.StringAddOpContext ctx) {
        //get dataTypes
        DataType dataTypeLeft = visit(ctx.left);
        DataType dataTypeRight = visit(ctx.right);

        //If neither of dataTypes is a String throw Model.TypeError
        if (!compareDataTypes(dataTypeLeft, DataTypeEnum.STRING.toString()) && !compareDataTypes(dataTypeRight, DataTypeEnum.STRING.toString())) {
            try {
                throw new TypeError("No string variable found. At line: " + ctx.start.getLine());
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
        }

        //visit children
        super.visitStringAddOp(ctx);

        //Return new String Model.DataType
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitStringWithExpression(DomboParser.StringWithExpressionContext ctx) {
        //visit children
        super.visitStringWithExpression(ctx);

        //return new String Model.DataType
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitStringVariable(DomboParser.StringVariableContext ctx) {
        //search the variable
        Type found = lookUpVariableInScopes(ctx.ID().getText());
        if (found == null) {
            try {
                throw new TypeError(ctx.ID().getText() + " not initialised. At line: " + ctx.start.getLine());
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
    public DataType visitStringValue(DomboParser.StringValueContext ctx) {
        //return new String Model.DataType
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitStringReadStatement(DomboParser.StringReadStatementContext ctx) {
        //visit children
        return super.visitStringReadStatement(ctx);
    }

    @Override
    public DataType visitReadCommand(DomboParser.ReadCommandContext ctx) {
        //return String dataType
        return new DataType(DataTypeEnum.STRING);
    }

    @Override
    public DataType visitReturnCommand(DomboParser.ReturnCommandContext ctx) {
        //get return Model.DataType
        DataType dataType = visit(ctx.expression());

        if (!checkReturnType(dataType, ctx.getStart().getLine())){
            return null;
        }

        //return dataType
        return dataType;
    }

    @Override
    public DataType visitReturnVoidCommand(DomboParser.ReturnVoidCommandContext ctx) {
        DataType dataType = new DataType(DataTypeEnum.VOID);

        if (!checkReturnType(dataType, ctx.getStart().getLine())){
            return null;
        }
        return dataType;
    }

    public boolean checkReturnType(DataType dataType, int lineNumber){
        Symbol symbol = null;

        //get the RETURN value
        if (scopes.get(1).lookUpVariable("RETURN") != null){
            symbol = scopes.get(1).lookUpVariable("RETURN");
        }

        //If return statement without method throw new Model.TypeError
        if (symbol == null){
            try {
                throw new TypeError("Return statement without method, At line: " + lineNumber);
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
            return false;
        }

        DataType temp = (DataType) symbol.type;
        //If returnType does not match with returning type throw new Model.TypeError
        if (!temp.getType().equalsIgnoreCase(dataType.getType())){
            try {
                throw new TypeError("Invalid return type, got: " + dataType.getType() + " expected: " + temp.getType() + "At line: " + lineNumber);
            } catch (TypeError typeError) {
                typeError.printStackTrace();
            }
            return false;
        }

        return true;
    }
}