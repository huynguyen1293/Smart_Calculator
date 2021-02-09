package calculator;

import java.math.BigInteger;
import java.util.*;

public class Main {
    static Map<String, BigInteger> variables = new HashMap<>();

    // Return the precedence of an operator
    // Higher priority means higher precedence
    static int getPrecedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
        }
        return -1;
    }

    // A method that converts an infix expression to postfix expression
    static String infixToPostfix(String[] exp) {
        String result = "";
        Deque<String> stack = new ArrayDeque<>();

        for (String s : exp) {
            if (s.matches("(\\d+|[a-zA-Z]+)")){         // Add the operand to the output
                result += s + " ";
            } else if (s.matches("\\(")) {              // Push the "(" to the stack
                stack.offerLast(s);
            } else if (s.matches("\\)")) {              // If the incoming element is an ")"
                if (!stack.contains("(")) {                   // If there is no other "(" in the stack
                    return "Invalid expression";              // Report the error
                }
                while (!stack.isEmpty() && !"(".equals(stack.peekLast())) {
                    result += stack.pollLast() + " ";                         // Pop and output from the stack
                }                                             // until an "(" is encountered.
                stack.pollLast();                   // Pop "(" out of the stack
            } else {                                // If an operator is encountered
                while (!stack.isEmpty() && getPrecedence(s) <= getPrecedence(stack.peekLast())){
                    result += stack.pollLast() + " ";     // Pop all the operators from the stack which are greater than
                }                                   // or equal to in precedence than that of the scanned operator
                stack.offerLast(s);                 // Push the scanned operator to the stack
            }
        }
        while (!stack.isEmpty()){
            if("(".equals(stack.peekLast())) {      // If there is at least one "(" left in the stack
                return "Invalid expression";        // Report the error
            }
            result += stack.pollLast() + " ";
        }
        return result;
    }

    static BigInteger postfixCalculator(String pf) {
        String[] array = pf.trim().split(" ");
        String result = "";
        Deque<BigInteger> stack = new ArrayDeque<>();

        for (int i = 0; i < array.length; i++) {
            if (array[i].matches("(\\d+|[a-zA-Z]+)")) {   // If the incoming element is a number/a variable
                BigInteger n = array[i].matches("\\d+") ? new BigInteger(array[i]) :
                        array[i].matches("[a-zA-Z]+") ? variables.get(array[i]) : BigInteger.ZERO;      // Retrieve the value of the variable
                stack.offerLast(n);                             // Push it into the stack
            } else {                                            // When encountered an operator
                BigInteger operand2 = stack.pollLast();         // Pop 2 operands from the stack
                BigInteger operand1 = stack.pollLast();
                BigInteger temp = BigInteger.ZERO;

                switch (array[i]) {         // Calculate the result base on the corresponding operator
                    case "+":
                        temp = operand1.add(operand2);
                        break;
                    case "-":
                        temp = operand1.subtract(operand2);
                        break;
                    case "*":
                        temp = operand1.multiply(operand2);
                        break;
                    case "/":
                        temp = operand1.divide(operand2);
                        break;
                    case "^":
                        temp = operand1.pow(operand2.intValue());
                        break;
                    default:
                        break;
                }
                stack.offerLast(temp);      // Push the result back to the stack
            }
        }
        return stack.pollLast();            // The remaining number is the result of the whole expression
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String helpStatement = "This program performs these 5 basic mathematical operations:\n" +
                                "addition, subtraction, multiplication, division, and exponentiation.\n" +
                                "This program also supports variables to store data.";

        while (scanner.hasNextLine()) {
            // Scan data from user
            String input = scanner.nextLine().replaceAll("\\s+", "");

            // Check if user inputs a command
            String commandRegex = "\\/[a-zA-Z]+";
            if (input.matches(commandRegex)){   // Command statement
                if ("/exit".equals(input)) {    // Exit command
                    System.out.println("Bye!");
                    break;
                } else if ("/help".equals(input)) { // Help command
                    System.out.println(helpStatement);
                    continue;
                } else {
                    System.out.println("Unknown command");
                    continue;
                }
            } else if (input.isEmpty()) {                   // When encounter empty line, output nothing
            } else if (input.matches("[a-zA-Z]+")) {  // Printing the value of a variable
                if (variables.containsKey(input)) {         // Check if the variable exists
                    System.out.println(variables.get(input));
                } else {
                    System.out.println("Unknown variable");
                }
            } else if (input.contains("=")) {                   // Assigning value to a variable
                String[] expression = input.split("=");

                if (expression.length != 2) {                   // Only supports single assignment format
                    System.out.println("Invalid assignment");
                }
                if (!expression[0].matches("[a-zA-Z]+")) {// Variable names must contain only characters (case sensitive)
                    System.out.println("Invalid identifier");
                }
                if (expression[1].matches("-?\\d+")) {                          // Assigning a numeric value
                    variables.put(expression[0], new BigInteger(expression[1]));
                } else if (expression[1].matches("-?[a-zA-Z]+")) {              // Assigning a value of another variable
                    if (expression[1].matches("-[a-zA-Z]+")) {                  // Negative cases
                        if (variables.containsKey(expression[1].substring(1))) {      // Checking if the variable exists
                            variables.put(expression[0], BigInteger.ONE.negate().multiply(variables.get(expression[1].substring(1))));
                        } else {
                            System.out.println("Unknown variable");
                        }
                    } else {                                                          // Positive cases
                        if (variables.containsKey(expression[1])) {                   // Checking if the variable exists
                            variables.put(expression[0], variables.get(expression[1]));
                        } else {
                            System.out.println("Unknown variable");
                        }
                    }
                } else {
                    System.out.println("Invalid assignment");
                }
            } else if (input.matches("[a-zA-Z]+=.*[^0-9].*")) {
                System.out.println("Invalid assignment");
            } else if (input.matches(".*[^a-zA-Z]+.*=\\d+")) {
                System.out.println("Invalid identifier");
            } else {
                if (input.matches(".*(\\*{2,}|/{2,}|\\^{2,}).*")) {
                    System.out.println("Invalid expression");
                    continue;
                }
                // Split input into operand(s) and operator(s)
                input = input.replaceAll("[*]", "*").
                            replaceAll("/", "/").
                            replaceAll("--", "+").
                            replaceAll("[+]-","-").
                            replaceAll("\\++", "+");

                String[] array = input.split("(?<=[-+*/()^])|(?=[-+*/()^])");
                String postFix = infixToPostfix(array);
                if ("Invalid expression".equals(postFix)) {
                    System.out.println("Invalid expression");
                    continue;
                }
                BigInteger result = postfixCalculator(postFix);

                System.out.println(result);
            }
        }
    }
}
