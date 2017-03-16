package Model;

/**
 * Class that is used to remember LocalParameters during bytecode generation
 */
public class LocalByteCodeParameter {
    private int position;
    private DataType dataType;

    public LocalByteCodeParameter(int position, DataType dataType) {
        this.position = position;
        this.dataType = dataType;
    }

    public int getPosition() {
        return position;
    }

    public DataType getDataType() {
        return dataType;
    }
}
