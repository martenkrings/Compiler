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

        while( true ) {
            // Ask for expression
            System.out.print("Test> ");
//            String line = s.nextLine();
            String line = "START function int test(int test1, boolean test2, int test3){\n function int functionInFunction(){return false;}  int a = do halo(1, 2);\n int b = do functionInFunction();\n return 2;}\nfunction int halo(int b, int c){return 1;}\n ";
            if( line.equals("exit") )
                break;

            // Eval
            System.out.println( "--> " + evaluate(line) );
        }
        System.out.println("KTHNXBYE");
    }
}
