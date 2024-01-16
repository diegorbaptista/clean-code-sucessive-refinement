package org.example.deprecated;

import org.example.Args;
import org.example.exceptions.ArgsException;

import java.text.ParseException;
import java.util.*;

@Deprecated
public class ArgsV3 {

    private class ArgsException extends IllegalArgumentException {
    }
    private abstract class ArgumentMarshaller {
        abstract void set(String s);
        abstract Object get();
    }
    private final class BooleanArgumentMarshaller extends ArgumentMarshaller {
        private boolean booleanValue = false;
        @Override
        public void set(String s) {
            this.booleanValue = true;
        }
        @Override
        public Object get() {
            return this.booleanValue;
        }
    }
    private class StringArgumentMarshaller extends ArgumentMarshaller {
        private String stringValue;
        @Override
        void set(String s) {
            this.stringValue = s;
        }

        @Override
        Object get() {
            return this.stringValue != null ? this.stringValue : "";
        }
    }
    private class IntegerArgumentMarshaller extends ArgumentMarshaller {
        private int intValue;
        @Override
        void set(String s) {
            this.intValue = Integer.parseInt(s);
        }

        @Override
        Object get() {
            return this.intValue;
        }
    }
    private final String schema;

    private final String[] args;
    private boolean valid = true;
    private final Set<Character> unexpectedArguments = new TreeSet<Character>();
    private final Map<Character, ArgumentMarshaller> marshalers = new HashMap<>();
    private final Set<Character> argsFound = new HashSet<Character>();
    private int currentArgument;
    private char errorArgumentId = '\0';
    private int numberOfArguments = 0;
    private ErrorCode errorCode = ErrorCode.OK;
    enum ErrorCode {
        OK,
        MISSING_STRING,
        MISSING_INTEGER,
        INVALID_INTEGER,
        UNEXPECTED_ARGUMENT;
    }
    public ArgsV3(String schema, String[] args) throws ParseException {
        this.schema = schema;
        this.args = args;
        valid = parse();
    }
    private boolean parse() throws ParseException {
        if (schema.isEmpty() && args.length == 0) {
            return true;
        }
        parseSchema();
        parseArguments();
        return valid;
    }

    private boolean parseSchema() throws ParseException {
        for(String element : schema.split(",")) {
            if (!element.isEmpty()) {
                var trimmedElement = element.trim();
                parseSchemaElement(trimmedElement);
            }
        }
        return true;
    }

    private void parseSchemaElement(String element) throws ParseException {
        char elementId = element.charAt(0);
        String elementTail = element.substring(1);
        validateSchemaElementId(elementId);
        if (isBooleanSchemaElement(elementTail)) {
            marshalers.put(elementId, new BooleanArgumentMarshaller());;
        }
        else if (isStringSchemaElement(elementTail)) {
            marshalers.put(elementId, new StringArgumentMarshaller());
        }
        else if (isIntegerSchemaElement(elementTail)) {
            marshalers.put(elementId, new IntegerArgumentMarshaller());
        }
    }

    private void validateSchemaElementId(Character elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException("Bad character: " + elementId + "in Args format: " + schema, 0);
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

    private boolean parseArguments() {
        for(currentArgument = 0; currentArgument < args.length; currentArgument++) {
            String arg = args[currentArgument];
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
        if(setArgument(argChar)) {
            argsFound.add(argChar);
        } else {
            unexpectedArguments.add(argChar);
            valid = false;
            errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
        }
    }

    private boolean setArgument(char argChar) {
        ArgumentMarshaller marshaler = marshalers.get(argChar);
        try {
            if (marshaler instanceof BooleanArgumentMarshaller) {
                setBooleanArg(marshaler);
            } else if (marshaler instanceof StringArgumentMarshaller) {
                setStringArg(marshaler);
            } else if (marshaler instanceof IntegerArgumentMarshaller) {
                setIntArg(marshaler);
            } else {
                return false;
            }
        } catch (ArgsException e) {
            valid = false;
            errorArgumentId = argChar;
            throw e;
        }

        return true;
    }

    private void setBooleanArg(ArgumentMarshaller m) {
        m.set("true");
    }

    private void setStringArg(ArgumentMarshaller m) {
        currentArgument++;
        try {
            m.set(args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSING_STRING;
            throw new ArgsException();
        }
    }

    private void setIntArg(ArgumentMarshaller m) {
        currentArgument++;
        try {
            var parameter = args[currentArgument];
            m.set(parameter);
        } catch (ArrayIndexOutOfBoundsException e) {
            errorCode = ErrorCode.MISSING_INTEGER;
            throw new ArgsException();
        } catch (NumberFormatException nfe) {
            errorCode = ErrorCode.INVALID_INTEGER;
            throw new ArgsException();
        }
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

    public String errorMessage() throws Exception {
        if (!unexpectedArgumentMessage().isEmpty()) {
            return unexpectedArgumentMessage();
        } else {
            return switch (errorCode) {
                case MISSING_STRING, MISSING_INTEGER -> String.format("Could not find string parameter for -%c.", errorArgumentId);
                case OK -> throw new Exception("TILT: Should not get here");
                case INVALID_INTEGER -> String.format("Value for parameter -%c is not a integer value", errorArgumentId);
                case UNEXPECTED_ARGUMENT -> unexpectedArgumentMessage();
            };
        }
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

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

}
