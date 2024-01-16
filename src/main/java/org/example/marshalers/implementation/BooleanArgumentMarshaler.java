package org.example.marshalers.implementation;

import org.example.exceptions.ArgsException;
import org.example.marshalers.ArgumentMarshaler;

import java.util.Iterator;

public class BooleanArgumentMarshaler implements ArgumentMarshaler {
    private boolean booleanValue;
    @Override
    public void set(Iterator<String> currentArgument) throws ArgsException {
        booleanValue = true;
    }

    @Override
    public Object get() {
        return this.booleanValue;
    }
}
