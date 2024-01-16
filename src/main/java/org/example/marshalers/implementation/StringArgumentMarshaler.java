package org.example.marshalers.implementation;

import org.example.Args;
import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;

import java.util.Iterator;

public class StringArgumentMarshaler implements ArgumentMarshaler {
    private String stringValue;
    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        if (!currentArgument.hasNext()) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING);
        }
        this.stringValue = currentArgument.next();
    }

    @Override
    public Object get() {
        return this.stringValue != null ? this.stringValue : "";
    }
}
