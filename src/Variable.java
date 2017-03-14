/**
 * Created by Sander on 13-3-2017.
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
