package org.example;

import org.example.deprecated.ArgsV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Second version of Args")
class ArgsV2Test {

    String[] validBooleanArg = {"-l"};
    String[] validBooleanAndStringArgs = {"-l", "-d*", "\\home\\output\\"};
    String[] invalidBooleanArg = {"l"};
    String[] invalidStringArgs = {"-l", "d*", "-p*"};

    @Test
    @DisplayName("it should be able to return a truthy boolean value from args")
    void shouldBeAbleToReturnABooleanValueFromArgs() throws ParseException {
        var args = new ArgsV2("l", validBooleanArg);
        assertTrue(args.getBoolean('l'));
    }
    @Test
    @DisplayName("it should be able to return a truthy boolean and a valid string from args")
    void shouldBeAbleToReturnAStringValueFromArgs() throws ParseException {
        var args = new ArgsV2("l,d*", validBooleanAndStringArgs);
        assertTrue(args.getBoolean('l'));
        assertEquals(validBooleanAndStringArgs[2], args.getString('d'));
    }
    @Test
    @DisplayName("it should not be able to return a truthy boolean from args")
    void shouldNotBeAbleToReturnATruthyBooleanValueFromArgs() throws ParseException {
        var args = new ArgsV2("l", invalidBooleanArg);
        assertFalse(args.getBoolean('l'));
        assertFalse(args.getBoolean('z'));
    }

    @Test
    @DisplayName("it should not be able to return a valid string from args")
    void shouldNotBeAbleToReturnAStringValueFromArgs() throws ParseException {
        var args = new ArgsV2("l,d*", invalidStringArgs);
        assertTrue(args.getString('d').isEmpty());
    }

}