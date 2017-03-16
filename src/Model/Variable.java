package Model;

import Model.DataType;

/**
 * Class that represents a variable
 * Used to 'remember' what a variable contains between different visitors
 */
public class Variable {
    String identifier;
    DataType dataType;

    public Variable(String identifier, DataType dataType) {
        this.identifier = identifier;
        this.dataType = dataType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DataType getDataType() {
        return dataType;
    }
}
