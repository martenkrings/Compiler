import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

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
        String line = "START function void main(){\n" +
                "    String s = \"Dit is de stomme standaard String\";\n" +
                "    if(true == false){\n" +
                "        s = \"in eerste if\";\n" +
                "    } else if(true == true){\n" +
                "        if(true == false){\n" +
                "            s = \"in if in if else\";\n" +
                "        } else if(true == true){\n" +
                "            s = \"CONGRATULATIONSSS\";\n" +
                "        }\n" +
                "    } else {\n" +
                "        s = \"in else\";\n" +
                "    }\n" +
                "     \n" +
                "    print s;\n" +
                "    return void;\n" +
                "}";
        System.out.println(line);

        System.out.println("--> ");

        //Print byteCodeResult
        ArrayList<String> byteCode = evaluate(line);
        String string = "";
        for (int i = 0; i < byteCode.size(); i++) {
            string += byteCode.get(i);
        }
        System.out.println(string);


        System.out.println("KTHNXBYE");
    }
}
