package com.example.dan.castdemo;

import java.io.Serializable;

/**
 * Created by Dan on 1/25/2016.
 */
public class Stock implements Serializable {
    public String name;
    public String ticker;

    public Stock(String substring, String completionText) {
        this.name = substring;
        this.ticker = completionText;
    }

    @Override
    public String toString() { return name; }
}
