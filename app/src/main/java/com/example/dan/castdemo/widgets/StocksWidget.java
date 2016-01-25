package com.example.dan.castdemo.widgets;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;


public class StocksWidget extends UIWidget {
    public static String HUMAN_NAME = "Stocks";

    public StocksWidget(Context context) {
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("stock1", "value1");
        json.put("stock2", "value2");
        return json;
    }
}
