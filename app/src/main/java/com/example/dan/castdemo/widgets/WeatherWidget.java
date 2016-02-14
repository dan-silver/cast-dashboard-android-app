package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.settingsFragments.WeatherSettings;

import org.json.JSONException;
import org.json.JSONObject;


public class WeatherWidget extends UIWidget {
    public static String HUMAN_NAME = "Weather";

    public WeatherWidget(Context context, Widget widget) {
        super(context, widget);
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("testkey0", "testvalue0");
        json.put("testkey1", "testvalue1");
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return widget.getOption(WeatherSettings.WEATHER_CITY).value;
    }
}
