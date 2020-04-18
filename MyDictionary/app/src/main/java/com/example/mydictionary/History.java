package com.example.mydictionary;

public class History {
    private String word;
    private String description;
    History(String word, String description){
        this.word = word;
        this.description = description;
    }

    public String getWord(){
        return word;
    }

    public String getDescription() {
        return description;
    }
}
