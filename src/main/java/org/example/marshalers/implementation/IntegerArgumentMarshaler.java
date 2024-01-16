package org.example.marshalers.implementation;

import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;

import java.util.Iterator;

public class IntegerArgumentMarshaler implements ArgumentMarshaler {
    private int intValue = 0;
    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        if (!currentArgument.hasNext()) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_INTEGER);
        }
        var parameter = currentArgument.next();
        try {
            this.intValue = Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_INTEGER, parameter);
        }
    }

    @Override
    public Object get() {
        return this.intValue;
    }
}
