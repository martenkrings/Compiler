import java.util.List;

/**
 * Created by Sander on 13-3-2017.
 */
public class Method {
    String identifier;
    MethodType methodType;

    public Method(String identifier, MethodType methodType) {
        this.identifier = identifier;
        this.methodType = methodType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DataType getReturnType(){
        return methodType.getReturnType();
    }

    public List<DataType> getParameterTypes(){
        return methodType.getParameters();
    }
}
