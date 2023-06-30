package com.vertxtexttest.domain;

public class Word {
    private final String name;
    private final int power;

    public Word(String name, int power){
        this.name = name;
        this.power = power;
    }

    public Word(String name){
        this.name = name;
        this.power =  name
                .toLowerCase()
                .chars()
                .map(number -> number - 96)
                .sum();
    }

    public Integer calculateDelta(Word otherWord){
        return Math.abs(power - otherWord.getPower());
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }
}
