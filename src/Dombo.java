import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
        DomboJasminGenerator jasminGenerator = new DomboJasminGenerator();
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
        String line = "String global = \"globalVariableHere\";\n" +
                "String empty;\n" +
                "\n" +
                "START function void main(){\n" +
                "   String test = \"\" append 3 append 4 append 5 append 6;\n" +
                "   \n" +
                "   return void;\n" +
                "}\n" +
                "\n" +
                "function String myFunction(){\n" +
                "    return \"banaan\";\n" +
                "}";
        System.out.println(line);

        System.out.println("--> ");

        //Print byteCodeResult
        ArrayList<String> byteCode = evaluate(line);

//        //make code a whole
//        String string = "";
//        for (int i = 0; i < byteCode.size(); i++) {
//            string += byteCode.get(i);
//        }

        //write result to a file
        try{
            PrintWriter writer = new PrintWriter("MyTest.j", "UTF-8");

            for (int i = 0; i < byteCode.size(); i++) {
                writer.write(byteCode.get(i));
            }

            //close write
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(string);


        System.out.println("KTHNXBYE");
    }
}
