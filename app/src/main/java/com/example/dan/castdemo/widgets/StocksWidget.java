package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Stock;
import com.example.dan.castdemo.Stock_Table;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.settingsFragments.StocksSettings;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONArray;
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


        // get the ids of the stocks
        List<WidgetOption> a = widget.getOptions(StocksSettings.STOCK_IN_LIST);

        ConditionGroup conditions = new ConditionGroup();
        for (WidgetOption stockOption : a) {
            conditions.or(Stock_Table._id.is(Long.parseLong(stockOption.value)));
        }

        // convert ids to tickers

        List<Stock> selectedStocks = new Select().from(Stock.class).where(conditions).queryList();

        JSONObject json = new JSONObject();
        JSONArray tickers = new JSONArray();
        for (Stock stock : selectedStocks) {
            tickers.put(stock.getTicker());
        }
        json.put("tickers", tickers);
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        List<WidgetOption> savedStocks = widget.getOptions(StocksSettings.STOCK_IN_LIST);
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
