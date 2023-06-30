package com.vertxtexttest.operators;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StringOperator {

    public int calculateCharValue(String algo){
        return algo.toLowerCase()
                .chars()
                .map(number -> number - 96)
                .sum();
    }
}
