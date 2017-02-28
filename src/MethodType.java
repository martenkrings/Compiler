import java.util.List;

/**
 * Created by Marten on 2/28/2017.
 */
public class MethodType extends Type {
    private DataType returnType;
    private List<DataType> parameters;

    public MethodType(DataType returnType, List<DataType> parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public void addParameter(DataType dataType) {
        parameters.add(dataType);
    }
}
