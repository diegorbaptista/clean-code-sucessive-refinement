package org.example.marshalers.implementation;

import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;

import java.util.Iterator;

public class DoubleArgumentMarshaler implements ArgumentMarshaler {
    private double doubleValue = 0;
    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        if (!currentArgument.hasNext()) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_DOUBLE);
        }
        var parameter = currentArgument.next();
        try {
            this.doubleValue = Double.parseDouble(parameter);
        } catch (NumberFormatException e) {
            throw new ArgsException(ArgsException.ErrorCode.INVALID_DOUBLE, parameter);
        }
    }

    @Override
    public Object get() {
        return this.doubleValue;
    }
}
