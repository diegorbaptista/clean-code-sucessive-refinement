package org.example;

import org.example.exceptions.ArgsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Final version of Args")
class ArgsTest {
    @Test
    @DisplayName("it should be able to return a truthy boolean value from args")
    void shouldBeAbleToReturnABooleanValueFromArgs() throws ParseException {
        var args = new Args("l", new String[] {"-l"});
        assertTrue(args.getBoolean('l'));
    }
    @Test
    @DisplayName("it should be able to return a valid string from args")
    void shouldBeAbleToReturnAStringValueFromArgs() throws ParseException {
        var args = new Args("d*", new String[] {"-d", "\\home\\output\\"});
        assertEquals("\\home\\output\\", args.getString('d'));
    }
    @Test
    @DisplayName("it should not be able to return a truthy boolean from args")
    void shouldNotBeAbleToReturnATruthyBooleanValueFromArgs() throws ParseException {
        var args = new Args("l", new String[] {"l"});
        assertFalse(args.getBoolean('l'));
        assertFalse(args.getBoolean('z'));
        assertFalse(args.has('l'));
    }

    @Test
    @DisplayName("it should not be able to return a valid string from args")
    void shouldNotBeAbleToReturnAStringValueFromArgs() throws ArgsException {
        assertThrows(ArgsException.class, () -> new Args("d*", new String[] {"-d"}));
    }

    @Test
    @DisplayName("it should be able to return two integer values from the args")
    void shouldBeAbleToReturnAIntegerArg() throws ParseException {
        var args = new Args("p#,e#", new String[] {"-p", "8080", "-e", "8181"});
        assertEquals(8080, args.getInt('p'));
        assertEquals(8181, args.getInt('e'));
    }

    @Test
    @DisplayName("ït should not be able to return an valid integer value from the args ")
    void shouldNotBeAbleToReturnAnIntegerValueFromArgs() throws ArgsException {
        assertThrows(ArgsException.class, () -> new Args("p#", new String[] {"-p", "ok"}));
    }

    @Test
    @DisplayName("it should throw an args exception when missing integer parameter")
    void shouldThrowAnExceptionWhenMissingIntegerParameter() throws ArgsException {
        assertThrows(ArgsException.class, () -> new Args("#p", new String[] {"-p"}));
    }

    @Test
    @DisplayName("it should throw an args exception when inform an invalid double parameter")
    void shouldThrowAnExceptionWhenAnInvalidDoubleParameter() {
        assertThrows(ArgsException.class, () -> new Args("v$", new String[] {"-v", "ok"}));
    }

    @Test
    @DisplayName("it should throw an args exception when inform an invalid double parameter")
    void shouldThrowAnExceptionWhenMissingDoubleParameter() {
        assertThrows(ArgsException.class, () -> new Args("v$", new String[] {"-v"}));
    }

    @Test
    @DisplayName("it should return a valid double value")
    void shouldReturnAValidDoubleValue() {
        var args = new Args("v$", new String[] {"-v", "2.33"});
        assertEquals(2.33, args.getDouble('v'));
    }

    @Test
    @DisplayName("it should return all values through all different typs of paramters")
    void shouldReturnAllValuesThroughAllTypesOfDifferentParameters() {
        var args = new Args("b,s*,i#,d$", new String[] {"-b", "-s", "valid string parameter", "-i", "8080", "-d", "2.11"});
        assertTrue(args.getBoolean('b'));
        assertEquals("valid string parameter", args.getString('s'));
        assertEquals(8080, args.getInt('i'));
        assertEquals(2.11, args.getDouble('d'));
    }

}