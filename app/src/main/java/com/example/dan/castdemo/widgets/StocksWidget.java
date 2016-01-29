package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.StockInfo;
import com.example.dan.castdemo.Stock_Table;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.WidgetOption_Table;
import com.example.dan.castdemo.settingsFragments.StocksSettings;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StocksWidget extends UIWidget {
    public static String HUMAN_NAME = "Stocks";
    Widget widget;

    public StocksWidget(Context context, Widget widget) {
        this.widget = widget;
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("stock1", "value1");
        json.put("stock2", "value2");
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        ConditionGroup conditions = new ConditionGroup();
        conditions.andAll(WidgetOption_Table.widgetForeignKeyContainer_id.eq(widget.id),
                WidgetOption_Table.key.is(StocksSettings.STOCK_IN_LIST));

        List<WidgetOption> savedStocks = new Select().from(WidgetOption.class).where(conditions).queryList();
        if (savedStocks.size() == 0) {
            return "Click to add stocks!";
        }


        // transform the stock ids into tickers, build a comma separated string

        ArrayList<String> stocksString = new ArrayList<>();



        for (WidgetOption option : savedStocks) {
            Stock stock = new Select().from(Stock.class).where(Stock_Table._id.is(Long.parseLong(option.value))).querySingle();

            // in the case of 1, use the company name
            if (savedStocks.size() == 1) {
                return stock.getName();
            }

            //otherwise use the ticker
            stocksString.add(stock.getTicker());
            if (stocksString.size() > 4) {
                break;
            }
        }


        return android.text.TextUtils.join(", ", stocksString);
    }
}
