package Model;

import java.util.List;

/**
 * Class used to keep track of the DataType of a method and the DataTypes of its parameters
 */
public class MethodType extends Type {
    private DataType returnType;
    private List<DataType> parameters;

    public MethodType(DataType returnType, List<DataType> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public List<DataType> getParameters() {
        return parameters;
    }

    public void addParameter(DataType dataType){
        parameters.add(dataType);
    }

    public DataType getReturnType() {
        return returnType;
    }
}
