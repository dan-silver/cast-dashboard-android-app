package com.silver.dan.castdemo;

import java.io.Serializable;

/**
 * Created by dan on 1/26/16.
 */
public class StockInfo implements Serializable {
    private String ticker;
    private String name;

    public StockInfo(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
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

}
