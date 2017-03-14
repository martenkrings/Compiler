import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.Scanner;

public class Dombo {
    public static ParseTreeProperty parseTreeProperty = new ParseTreeProperty();
    /*
     * Evalute the entered line.
     * @param line  A line conforming to the grammar Calc.g4
     * @return      The evaluated value.
     */
    private static Object evaluate(String line) {
        ANTLRInputStream inputStream = new ANTLRInputStream(line);

        // Create lexer and run scanner to create stream of tokens
        DomboLexer lexer = new DomboLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create parser and feed it the tokens
        DomboParser parser = new DomboParser(tokens);
        ParseTree expression = parser.program();

        // Evaluate by running the visitor
        DomboTypeChecker evaluator = new DomboTypeChecker();
        DataType value = evaluator.visit(expression);
        return value;
    }

    /*
     * Main method.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        // Ask for expression
        System.out.print("Test> ");
        String line = "int globalVar = 0;\n" +
                "if(true){ \n" +
                "   int vergeetMij = 1; \n" +
                "} \n" +
                "\n" +
                "function int functionOutOfFunction(){\n" +
                "        return 1;\n" +
                "}    \n" +
                "\n" +
                "START function void main(){\n" +
                "    boolean t = true;\n" +
                "   \n" +
                "    if(true){\n" +
                "       return void; \n" +
                "    }\n" +
                "    int i = globalVar; \n" +
                "    String string = \"s2\";\n" +
                "    return void;\n" +
                "}\n" +
                "\n" +
                "function void halo(String b, int c){\n" +
                "    String s2 = \"teststring\" + b ; \n" +
                "    return void;\n" +
                "}";
        System.out.println(line);

        // Eval
        System.out.println("--> " + evaluate(line));
        System.out.println("KTHNXBYE");
    }
}
