package com.example.habitmoodtracker.api;

import com.google.gson.annotations.SerializedName;

public class QuoteResponse {

    @SerializedName("content")
    private String quote;

    @SerializedName("author")
    private String author;

    public QuoteResponse() {
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}