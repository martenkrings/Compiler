import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Dombo {
    public static ParseTreeProperty parseTreeProperty = new ParseTreeProperty();
    /*
     * Evalute the entered line.
     * @param line  A line conforming to the grammar Calc.g4
     * @return      The evaluated value.
     */
    private static ArrayList<String> evaluate(String line) {
        ANTLRInputStream inputStream = new ANTLRInputStream(line);

        // Create lexer and run scanner to create stream of tokens
        DomboLexer lexer = new DomboLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create parser and feed it the tokens
        DomboParser parser = new DomboParser(tokens);
        ParseTree expression = parser.program();

        // Evaluate by running the visitor
        DomboTypeChecker evaluator = new DomboTypeChecker();
        evaluator.visit(expression);

        // Generate bytecode
        JasminGenerator jasminGenerator = new JasminGenerator();
        ArrayList<String> value = jasminGenerator.visit(expression);
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
                "\n" +
                "if ( false ) { \n" +
                "    int i = 0;\n" +
                "    }\n" +
                "\n" +
                "function int functionInFunction(){\n" +
                "        boolean test = false == false;\n" +
                "        return do halo(\"test\", 2);\n" +
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
                "function int halo(String b, int c){\n" +
                "    String s2 = \"teststring\" + b ; \n" +
                "    return 1;\n" +
                "}";
        System.out.println(line);

        System.out.println("--> ");

        //Print byteCodeResult
        ArrayList<String> byteCode = evaluate(line);
        for (int i = 0; i < byteCode.size(); i++){
            System.out.println(byteCode.get(i));
        }



        System.out.println("KTHNXBYE");
    }
}
