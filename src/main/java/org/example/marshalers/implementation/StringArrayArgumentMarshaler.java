package org.example.marshalers.implementation;

import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;

import java.util.Iterator;

public class StringArrayArgumentMarshaler implements ArgumentMarshaler {
    private String[] stringArrayValue;

    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        if (!currentArgument.hasNext()) {
            throw new ArgsException(ArgsException.ErrorCode.MISSING_STRING_ARRAY);
        }
        this.stringArrayValue = currentArgument.next().trim().split("\\s*,\\s*");
    }

    @Override
    public Object get() {
        return this.stringArrayValue;
    }
}
