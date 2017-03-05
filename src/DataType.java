/**
 * Created by Marten on 2/28/2017.
 */
public class DataType extends Type{
    private DataTypeEnum type;

    public DataType(DataTypeEnum type) {
        this.type = type;
    }

    public DataType(String type) {
        if (type.equalsIgnoreCase("int")){
            this.type = DataTypeEnum.INT;
        } else if (type.equalsIgnoreCase("boolean")){
            this.type = DataTypeEnum.BOOLEAN;
        } else if (type.equalsIgnoreCase("string")){
            this.type = DataTypeEnum.STRING;
        }
    }

    public String getType() {
        return type.toString();
    }
}
