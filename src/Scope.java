import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marten on 2/28/2017.
 */
public class Scope {
    private Scope parentScope;
    private Map<String, Symbol> symbolTable;

    public Scope() {
        this.parentScope = null;
        this.symbolTable = new HashMap<>();
    }

    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.symbolTable = new HashMap<>();
    }

    public Symbol declareVariable(String identifier, DataType dataType) {
        Symbol s = new Symbol(identifier, dataType);

        symbolTable.put(identifier, s);

        return s;
    }

    public Symbol declareMethod(String identifier, DataType returnType, List<DataType> parameters) {
        Symbol s = new Symbol(identifier, new MethodType(returnType, parameters));

        symbolTable.put(identifier, s);

        return s;
    }

    public Symbol lookUpVariable(String identifier) {
        return symbolTable.get(identifier);
    }

    public Symbol lookUpMethod(String identifier) {
        return symbolTable.get(identifier);
    }

    public Scope openScope() {
        return new Scope(this);
    }

    public Scope closeScope() {
        return parentScope;
    }
}
