package org.example.exceptions;

public class ArgsException extends IllegalArgumentException {
    private char errorArgumentId;
    private String errorParameter;
    private final ErrorCode errorCode;

    public enum ErrorCode {
        INVALID_SCHEMA,
        INVALID_FORMAT,
        INVALID_ARGUMENT_NAME,
        MISSING_STRING,
        MISSING_INTEGER,
        MISSING_DOUBLE,
        INVALID_INTEGER,
        INVALID_DOUBLE,
        UNEXPECTED_ARGUMENT;
    }

    public ArgsException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ArgsException(ErrorCode errorCode, char errorArgumentId) {
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
    }

    public ArgsException(ErrorCode errorCode, String errorParameter) {
        this.errorCode = errorCode;
        this.errorParameter = errorParameter;
    }

    public ArgsException(ErrorCode errorCode, char errorArgumentId, String errorParameter) {
        this.errorCode = errorCode;
        this.errorArgumentId = errorArgumentId;
        this.errorParameter = errorParameter;
    }

    public void setErrorArgumentId(char errorArgumentId) {
        this.errorArgumentId = errorArgumentId;
    }

    public String getMessage() {
        return switch (errorCode) {
            case INVALID_SCHEMA -> "Invalid args schema";
            case INVALID_FORMAT -> String.format("Invalid format for argument %c and value %s", errorArgumentId, errorParameter);
            case INVALID_ARGUMENT_NAME -> String.format("Invalid argument name for %c", errorArgumentId);
            case MISSING_STRING, MISSING_INTEGER, MISSING_DOUBLE ->
                    String.format("Could not find parameter for -%c.", errorArgumentId);
            case INVALID_INTEGER ->
                    String.format("Value for parameter -%c is not a integer value for %s", errorArgumentId, errorParameter);
            case INVALID_DOUBLE ->
                    String.format("Value for parameter -%c is not a double value for %s", errorArgumentId, errorParameter);
            case UNEXPECTED_ARGUMENT -> String.format("Argument %c unexpected", errorArgumentId);
        };
    }
}
