package com.vertxtexttest.dto;

public class FindWordDto {
    private String text;
    private String wordListId;

    public String getWordListId() {
        return wordListId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWordListId(String wordListId) {
        this.wordListId = wordListId;
    }
}
