package com.silver.dan.castdemo.util;

import android.content.Context;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.silver.dan.castdemo.Stock;
import com.silver.dan.castdemo.WidgetDatabase;

import java.io.IOException;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;


public class StockUtils {
    public static void insertFromCSV(final Context context) {

        DatabaseDefinition database = FlowManager.getDatabase(WidgetDatabase.class);
        database.executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                CSVReader reader;
                try {
                    reader = new CSVReader(new InputStreamReader(context.getAssets().open("stocks.csv")));
                    String[] line;

                    while ((line = reader.readNext()) != null) {
                        Stock s = new Stock();
                        s.setInfo(line[1], line[0]);
                        s.insert();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
