package org.example;

import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;
import org.example.marshalers.implementation.*;

import java.util.*;

public class Args {
    private final String schema;
    private final List<String> args;
    private final Map<Character, ArgumentMarshaler> marshalers = new HashMap<>();
    private final Set<Character> argsFound = new HashSet<Character>();
    private Iterator<String> currentArgument;

    public Args(String schema, String[] args) throws ArgsException {
        this.schema = schema;
        this.args = Arrays.asList(args);
        parse();
    }

    private void parse() throws ArgsException {
        if (schema.isEmpty() && args.isEmpty()) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_SCHEMA);
        }
        parseSchema();
        parseArguments();
    }

    private void parseSchema() throws ArgsException {
        for (String element : schema.split(",")) {
            if (!element.isEmpty()) {
                parseSchemaElement(element.trim());
            }
        }
    }

    private void parseSchemaElement(String element) throws ArgsException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail)) {
            marshalers.put(elementId, new BooleanArgumentMarshaler());
        } else if (isStringSchemaElement(elementTail)) {
            marshalers.put(elementId, new StringArgumentMarshaler());
        } else if (isIntegerSchemaElement(elementTail)) {
            marshalers.put(elementId, new IntegerArgumentMarshaler());
        } else if (isDoubleSchemaElement(elementTail)) {
            marshalers.put(elementId, new DoubleArgumentMarshaler());
        } else if (isStringArraySchemaElement(elementTail)) {
            marshalers.put(elementId, new StringArrayArgumentMarshaler());
        } else {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_FORMAT, elementId, elementTail);
        }
    }

    private void validateSchemaElementId(Character elementId) throws ArgsException {
        if (!Character.isLetter(elementId)) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_ARGUMENT_NAME, elementId);
        }
    }

    private boolean isIntegerSchemaElement(String elementTail) {
        return elementTail.equals("#");
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.isEmpty();
    }

    private boolean isDoubleSchemaElement(String elementTail) {
        return elementTail.equals("##");
    }

    private boolean isStringArraySchemaElement(String elementTrail) {
        return elementTrail.equals("[*]");
    }

    private void parseArguments() {
        for (currentArgument = args.iterator(); currentArgument.hasNext(); ) {
            String arg = currentArgument.next();
            parseArgument(arg);
        }
    }

    private void parseArgument(String arg) {
        if (arg.startsWith("-")) {
            parseElements(arg);
        }
    }

    private void parseElements(String arg) {
        for (int i = 1; i < arg.length(); i++) {
            parseElement(arg.charAt(i));
        }
    }

    private void parseElement(char argChar) {
        if (setArgument(argChar)) {
            argsFound.add(argChar);
        } else {
            throw new ArgsException(ArgsException.ErrorCode.UNEXPECTED_ARGUMENT, argChar);
        }
    }

    private boolean setArgument(char argChar) {
        ArgumentMarshaler marshaler = marshalers.get(argChar);
        if (marshaler == null) {
            return false;
        }
        try {
            marshaler.set(currentArgument);
            return true;
        } catch (ArgsException e) {
            e.setErrorArgumentId(argChar);
            throw e;
        }
    }

    public boolean getBoolean(char arg) {
        var am = marshalers.get(arg);
        try {
            return am != null && (Boolean) am.get();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String getString(char arg) {
        try {
            return (String) marshalers.get(arg).get();
        } catch (ClassCastException e) {
            return "";
        }
    }

    public int getInt(char arg) {
        try {
            return (Integer) marshalers.get(arg).get();
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public double getDouble(char arg) {
        try {
            return (Double) marshalers.get(arg).get();
        } catch (ClassCastException e) {
            return 0;
        }
    }

    public String[] getStringArray(char arg) {
        try {
            return (String[]) marshalers.get(arg).get();
        } catch (ClassCastException e) {
            return new String[] {};
        }
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

}
