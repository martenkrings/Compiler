import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.*;
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
        System.out.print("Enter dombo file name> ");

        //Read Filename
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.nextLine();
        scanner.close();

        //readFile
        String line = null;
        try {
            Scanner fileScanner = new Scanner(new File(fileName + ".dombo"));
            line = fileScanner.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file or file corrupt");

            System.out.println("\nError message:");
            System.out.println(e.getMessage());
        }

        //print line breaker
        System.out.println("--> ");

        //Print byteCodeResult
        ArrayList<String> byteCode = evaluate(line);

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

        System.out.println("KTHNXBYE");
    }
}
