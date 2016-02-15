package com.example.dan.castdemo.widgets;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.settingsFragments.WeatherSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class WeatherWidget extends UIWidget {
    public static String HUMAN_NAME = "Weather";

    public WeatherWidget(Context context, Widget widget) {
        super(context, widget);
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lat", widget.getOption(WeatherSettings.WEATHER_LAT).value);
        json.put("lng", widget.getOption(WeatherSettings.WEATHER_LNG).value);
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {

        return WeatherSettings.getNameFromCoordinates(context, widget);
    }
}
