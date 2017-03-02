/**
 * Created by Marten on 2/28/2017.
 */
public class DataType extends Type{
    private DataTypeEnum type;

    public DataType(DataTypeEnum type) {
        this.type = type;
    }

    public DataType(String type) {
        switch (type){
            case "INT" :
                this.type = DataTypeEnum.INT;
                break;
            case "BOOLEAN":
                this.type = DataTypeEnum.BOOLEAN;
                break;
            case "STRING":
                this.type = DataTypeEnum.STRING;
                break;
        }
    }

    public String getType() {
        return type.toString();
    }
}
