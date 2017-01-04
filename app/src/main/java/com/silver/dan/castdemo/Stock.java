package com.silver.dan.castdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDoneException;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.silver.dan.castdemo.Util.StockUtils;

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

    static void insertAllStocks(Context context) {
        //check if stocks table is empty
        long count = 0;
        try {
            count = new Select().from(Stock.class).count();
        } catch (SQLiteDoneException e) {
            // if the table isn't created yet
        }

        if (count == 0) {
            StockUtils.insertFromCSV(context);
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
