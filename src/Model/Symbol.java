package Model;

/**
 * Simple representation of a variable or method
 */
public class Symbol {
    public String name;
    public Type type;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
