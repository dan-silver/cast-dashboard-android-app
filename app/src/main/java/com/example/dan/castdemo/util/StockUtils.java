package com.example.dan.castdemo.util;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.WidgetDatabase;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
