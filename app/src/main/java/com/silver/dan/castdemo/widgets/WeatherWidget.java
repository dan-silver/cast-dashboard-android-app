package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.WeatherSettings;

import org.json.JSONException;
import org.json.JSONObject;


public class WeatherWidget extends UIWidget {
    public static String HUMAN_NAME = "Weather";

    public WeatherWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        widget.initOption(WeatherSettings.WEATHER_LAT, "47.6025269");
        widget.initOption(WeatherSettings.WEATHER_LNG, "-122.3411561");
        widget.initOption(WeatherSettings.WEATHER_CITY, "Seattle, WA");
        widget.initOption(WeatherSettings.WEATHER_UNITS, 0); // default to fahrenheit
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lat",   widget.getOption(WeatherSettings.WEATHER_LAT).value);
        json.put("lng",   widget.getOption(WeatherSettings.WEATHER_LNG).value);
        json.put("units", widget.getOption(WeatherSettings.WEATHER_UNITS).value);
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {

        return WeatherSettings.getNameFromCoordinates(context, widget);
    }
}
