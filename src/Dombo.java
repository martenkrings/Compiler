import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Scanner;
public class Dombo {
    /*
     * Evalute the entered line.
     * @param line  A line conforming to the grammar Calc.g4
     * @return      The evaluated value.
     */
    private static Object evaluate( String line ) {
        ANTLRInputStream inputStream = new ANTLRInputStream( line );

        // Create lexer and run scanner to create stream of tokens
        DomboLexer lexer = new DomboLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create parser and feed it the tokens
        DomboParser parser = new DomboParser(tokens);
        ParseTree expression = parser.program();

        // Evaluate by running the visitor
        DomboTypeChecker evaluator = new DomboTypeChecker();
        DataType value = evaluator.visit(expression);

        return value.getType();
    }

    /*
     * Main method.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

//        while( true ) {
            // Ask for expression
            System.out.print("Test> ");
//            String line = s.nextLine();
            String line = "START function int test(int test1, boolean test2, int test3){\n function int functionInFunction(){boolean test = true == false;" +
                    " return 1;}  int a = do halo(1, 2);\n boolean b1 = false; \n boolean b2 = true; \n boolean random = b1 < b2;\n return 2;}" +
                    "\nfunction int halo(int b, int c){\n " +
                    "String s1 = \"\"; \n String s2 = s1 + \" testString \" ; \n s2 = false; \n return 1;}\n ";
            System.out.println(line);
//            if( line.equals("exit") )
//                break;

            // Eval
            System.out.println( "--> " + evaluate(line) );
//        }
        System.out.println("KTHNXBYE");
    }
}
