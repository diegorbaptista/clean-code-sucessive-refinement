package org.example;

import org.example.deprecated.ArgsV3;
import org.example.exceptions.ArgsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Third version of Args")
class ArgsV3Test {
    String[] validBooleanArg = {"-l"};
    String[] validBooleanAndStringArgs = {"-l", "-d*", "\\home\\output\\"};
    String[] invalidBooleanArg = {"l"};
    String[] invalidStringArgs = {"-l", "d*", "-p*"};
    String[] validIntegerArg = {"-p", "8080", "-e", "8181"};
    String[] invalidIntegerArg = {"p", "ok", "-y", "error", "-e"};

    @Test
    @DisplayName("it should be able to return a truthy boolean value from args")
    void shouldBeAbleToReturnABooleanValueFromArgs() throws ParseException {
        var args = new ArgsV3("l", validBooleanArg);
        assertTrue(args.getBoolean('l'));
    }
    @Test
    @DisplayName("it should be able to return a truthy boolean and a valid string from args")
    void shouldBeAbleToReturnAStringValueFromArgs() throws ParseException {
        var args = new ArgsV3("l,d*", validBooleanAndStringArgs);
        assertTrue(args.getBoolean('l'));
        assertEquals(validBooleanAndStringArgs[2], args.getString('d'));
    }
    @Test
    @DisplayName("it should not be able to return a truthy boolean from args")
    void shouldNotBeAbleToReturnATruthyBooleanValueFromArgs() throws ParseException {
        var args = new ArgsV3("l", invalidBooleanArg);
        assertFalse(args.getBoolean('l'));
        assertFalse(args.getBoolean('z'));
    }

    @Test
    @DisplayName("it should not be able to return a valid string from args")
    void shouldNotBeAbleToReturnAStringValueFromArgs() throws ParseException {
        var args = new ArgsV3("l,d*", invalidStringArgs);
        assertTrue(args.getString('d').isEmpty());
    }

    @Test
    @DisplayName("it should be able to return two integer values from the args")
    void shouldBeAbleToReturnAIntegerArg() throws ParseException {
        var args = new ArgsV3("p#,e#", validIntegerArg);
        assertEquals(Integer.parseInt(validIntegerArg[1]), args.getInt('p'));
        assertEquals(Integer.parseInt(validIntegerArg[3]), args.getInt('e'));
    }

    @Test
    @DisplayName("ït should not be able to return an valid integer value from the args")
    void shouldNotBeAbleToReturnAnIntegerValueFromArgs() throws ParseException {
        assertThrows(ArgsException.class, () -> new ArgsV3("p#,y#,e#", invalidIntegerArg));
    }

}