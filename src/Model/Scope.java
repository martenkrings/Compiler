package Model;

import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a scope
 * Contains a map filled with Symbols
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

    public Scope(Scope parentScope, DataType dataType){
        this.parentScope = parentScope;
        this.symbolTable = new HashMap<>();
        symbolTable.put("RETURN", new Symbol("RETURN", dataType));
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

    @Nullable
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

    public Scope getParentScope() {
        return parentScope;
    }
}
