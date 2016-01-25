package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;


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
        return "MSFT, T, TGLDX";
    }
}
