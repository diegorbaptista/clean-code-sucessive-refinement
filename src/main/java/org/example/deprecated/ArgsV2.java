package org.example.deprecated;

import java.text.ParseException;
import java.util.*;

@Deprecated
public class ArgsV2 {
    private final String schema;
    private final String[] args;
    private boolean valid = true;
    private final Set<Character> unexpectedArguments = new TreeSet<Character>();
    private final Map<Character, Boolean> booleanArgs = new HashMap<Character, Boolean>();
    private final Map<Character, String> stringArgs = new HashMap<Character, String>();
    private final Set<Character> argsFound = new HashSet<Character>();
    private int currentArgument;
    private char errorArgument = '\0';
    private int numberOfArguments = 0;
    private ErrorCode errorCode = ErrorCode.OK;
    enum ErrorCode {
        OK,
        MISSING_STRING;
    }
    public ArgsV2(String schema, String[] args) throws ParseException {
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
            parseBooleanSchemaElement(elementId);
        }
        else if (isStringSchemaElement(elementTail)) {
            parseStringSchemaElement(elementId);
        }
    }

    private void validateSchemaElementId(Character elementId) throws ParseException {
        if (!Character.isLetter(elementId)) {
            throw new ParseException("Bad character: " + elementId + "in Args format: " + schema, 0);
        }
    }

    private void parseStringSchemaElement(char elementId) {
        stringArgs.put(elementId, "");
    }

    private boolean isStringSchemaElement(String elementTail) {
        return elementTail.equals("*");
    }

    private boolean isBooleanSchemaElement(String elementTail) {
        return elementTail.isEmpty();
    }

    private void parseBooleanSchemaElement(char element) {
        booleanArgs.put(element, false);
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
        }
    }

    private boolean setArgument(char argChar) {
        var set = true;
        if (isBoolean(argChar)) {
            setBooleanArg(argChar, true);
        } else if (isString(argChar)) {
            setStringArg(argChar, "");
        } else {
            set = false;
        }
        return set;
    }

    private void setBooleanArg(char argChar, boolean value) {
        booleanArgs.put(argChar, value);
    }

    private boolean isBoolean(char argChar) {
        return booleanArgs.containsKey(argChar);
    }

    private void setStringArg(char argChar, String value) {
        currentArgument++;
        try {
            stringArgs.put(argChar, args[currentArgument]);
        } catch (ArrayIndexOutOfBoundsException e) {
            valid = false;
            errorArgument = argChar;
            errorCode = ErrorCode.MISSING_STRING;
        }
    }

    private boolean isString(char argChar) {
        return stringArgs.containsKey(argChar);
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
                case MISSING_STRING -> String.format("Could not find string parameter for -%c.", errorArgument);
                case OK -> throw new Exception("TILT: Should not get here");
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
        return falseIfNull(booleanArgs.get(arg));
    }

    private boolean falseIfNull(Boolean b) {
        return b != null && b;
    }

    public String getString(char arg) {
        return blankIfNull(stringArgs.get(arg));
    }

    private String blankIfNull(String s) {
        return s != null ? s : "";
    }

    public boolean has(char arg) {
        return argsFound.contains(arg);
    }

    public boolean isValid() {
        return valid;
    }

}
