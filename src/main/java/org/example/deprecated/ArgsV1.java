package org.example.deprecated;

import java.util.*;

@Deprecated
public class ArgsV1 {
    private final String schema;
    private final String[] args;
    private final boolean valid;
    private final Set<Character> unexpectedArguments = new TreeSet<Character>();
    private final Map<Character, Boolean> booleanArgs = new HashMap<Character, Boolean>();
    private int numberOfArguments = 0;

    public ArgsV1(String schema, String[] args) {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }

    public boolean isValid() {
        return valid;
    }
    private boolean parse() {
        if (schema.isEmpty() && args.length == 0) {
            return true;
        }
        parseSchema();
        parseArguments();
        return unexpectedArguments.isEmpty();
    }

    private boolean parseSchema() {
        for(String element : schema.split(",")) {
           parseSchemaElement(element);
        }
        return true;
    }

    private void parseSchemaElement(String element) {
        if (element.length() == 1) {
            parseBooleanSchemaElement(element);
        }
    }
    private void parseBooleanSchemaElement(String element) {
        char c = element.charAt(0);
        if (Character.isLetter(c)) {
            booleanArgs.put(c, false);
        }
    }

    private boolean parseArguments() {
        for (String arg : args) {
            parseArgument(arg);
        }
        return true;
    }

    private void parseArgument(String arg) {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) {
        for (int i = 0; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) {
        if (isBoolean(argChar)) {
            numberOfArguments++;
            setBooleanArg(argChar, true);
        } else {
            unexpectedArguments.add(argChar);
        }
    }

    private void setBooleanArg(char argChar, boolean value) {
        booleanArgs.put(argChar, value);
    }

    private boolean isBoolean(char argChar) {
        return booleanArgs.containsKey(argChar);
    }

    public int cardinality() {
        return numberOfArguments;
    }

    public String usage() {
        if (!schema.isEmpty()) {
            return "-["+schema+"]";
        } else {
            return "";
        }
    }

    public String errorMessage() {
        if (unexpectedArguments.isEmpty()) return "";
        return unexpectedArgumentMessage();
    }

    public String unexpectedArgumentMessage() {
        StringBuilder message = new StringBuilder("Argument(s) -");
        for(char c : unexpectedArguments) {
            message.append(c);
        }
        message.append(" unexpected");
        return message.toString();
    }

    public boolean getBoolean(char arg) {
        return booleanArgs.get(arg);
    }

}
