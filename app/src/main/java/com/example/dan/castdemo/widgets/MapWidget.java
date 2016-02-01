package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;


public class MapWidget extends UIWidget {
    public static String HUMAN_NAME = "Map";

    public MapWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("long", "value1");
        json.put("lat", "value2");
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return "Traffic Map of Chesterfield, MO";
    }
}
