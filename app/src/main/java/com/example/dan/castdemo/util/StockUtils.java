package com.example.dan.castdemo.util;

import android.content.Context;

import com.example.dan.castdemo.Stock;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;


public class StockUtils {
    public static void insertFromCSV(Context context) {
        CSVReader reader;
        Stock s;
        FlowContentObserver observer = new FlowContentObserver();
        try {
            reader = new CSVReader(new InputStreamReader(context.getAssets().open("stocks.csv")));
            String[] line;
            observer.beginTransaction();

            while ((line = reader.readNext()) != null) {
                s = new Stock();
                s.setInfo(line[1], line[0]);
                s.save();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            observer.endTransactionAndNotify();
        }

    }
}
