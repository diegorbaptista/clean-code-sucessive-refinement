package org.example;

import org.example.deprecated.ArgsV2;

import java.text.ParseException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws ParseException {
        System.out.println(Arrays.toString(args));
        //ArgsV1 arg = new ArgsV1("l", args);
        ArgsV2 arg = new ArgsV2("l,d*", args);

        var logging = arg.getBoolean('l');
        var directory = arg.getString('d');

        executeApplication(logging, directory);
    }

    public static void executeApplication(boolean logging, String directory) {
        System.out.println("Application is starting...");
        System.out.println("Logging is " + (logging ?  "enabled" : "disabled"));
        System.out.println("Directory is " + (directory.isEmpty() ? "not informed" : directory));
    }
}