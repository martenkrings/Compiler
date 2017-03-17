package Model;

import java.util.HashMap;
import java.util.List;

/**
 * Class that represents a method
 * Used to 'remember' what a method contains between different visitors
 * Used ro keep track of localVariables during java byte code generation
 */
public class Method {
    private String identifier;
    private MethodType methodType;

    //Contains localVariables findable by their identifier
    private HashMap<String, LocalByteCodeParameter> localVariables;

    /**
     * Default constructor
     * @param identifier of method
     * @param methodType Return type of method
     */
    public Method(String identifier, MethodType methodType) {
        this.identifier = identifier;
        this.methodType = methodType;
        this.localVariables = new HashMap<>();
    }

    /**
     * Get an localVariable by its identifier
     * @param identifier of localVariable
     * @return the found localVariable or null
     */
    public LocalByteCodeParameter getLocalVariable(String identifier){
        return localVariables.get(identifier);
    }

    public HashMap<String, LocalByteCodeParameter> getLocalVariables() {
        return localVariables;
    }

    /**
     * Adds an localVariable, put its at the next empty index
     * @param variableIdentifier identifier
     * @param dataType dataType of new localVariable
     */
    public void storeLocalVariable(String variableIdentifier, DataType dataType){
        //If this is called multiple times for the same variable change nothing
        if (localVariables.get(variableIdentifier) != null){
            return;
        }

        //add new variable
        localVariables.put(variableIdentifier, new LocalByteCodeParameter(localVariables.size(), dataType));
    }
}
