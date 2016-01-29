package com.example.dan.castdemo;

import java.io.Serializable;

/**
 * Created by dan on 1/26/16.
 */
public class StockInfo implements Serializable {
    private String ticker;
    private String name;
    private long id;

    public StockInfo(String ticker, String name, long id) {
        this.ticker = ticker;
        this.name = name;
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
