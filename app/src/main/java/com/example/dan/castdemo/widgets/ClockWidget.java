package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;


public class ClockWidget extends UIWidget {
    public static String HUMAN_NAME = "Clock";

    public ClockWidget(Context context, Widget widget) {
        super(context, widget);
    }


    @Override
    public JSONObject getContent() throws JSONException {
        return new JSONObject();
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return "Clock Widget";
    }
}
