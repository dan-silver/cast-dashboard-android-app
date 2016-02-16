package com.silvr.dan.castdemo.util;

import android.content.Context;

import com.silvr.dan.castdemo.Stock;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;


public class StockUtils {
    public static void insertFromCSV(Context context) {
        CSVReader reader;
        Stock s;
        FlowContentObserver observer = new FlowContentObserver();
        List<Stock> stocks = new ArrayList<>();
        try {
            reader = new CSVReader(new InputStreamReader(context.getAssets().open("stocks.csv")));
            String[] line;
            observer.beginTransaction();

            while ((line = reader.readNext()) != null) {
                s = new Stock();
                s.setInfo(line[1], line[0]);
                stocks.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            observer.endTransactionAndNotify();
        }

        // insert all stocks in a transaction
        TransactionManager.getInstance().addTransaction(new SaveModelTransaction<>(ProcessModelInfo.withModels(stocks)));


    }
}