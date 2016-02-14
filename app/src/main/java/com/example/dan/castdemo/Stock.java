package com.example.dan.castdemo;

import android.content.Context;
import android.util.Log;

import com.example.dan.castdemo.util.StockUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

@ModelContainer
@Table(database = WidgetDatabase.class)

public class Stock extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private long _id;

    @Column
    private String name;

    @Column
    private String ticker;

    public void setInfo(String name, String ticker) {
        this.name = name;
        this.ticker = ticker;
    }

    public Stock() {
    }

    public static void insertAllStocks(Context context) {
        //check if stocks table is empty

        Stock stock = new Select().from(Stock.class).querySingle();

        // stocks table already populated
        if (stock == null) {
            long startTime = System.currentTimeMillis();
            StockUtils.insertFromCSV(context);
            long endTime = System.currentTimeMillis();
            Log.v(MainActivity.TAG, "time to insert stocks: " + (endTime - startTime) + " ms");

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }
}
