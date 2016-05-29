package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.WeatherSettings;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class WeatherWidget extends UIWidget {

    public WeatherWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        widget.initOption(WeatherSettings.WEATHER_LAT, "47.6025269");
        widget.initOption(WeatherSettings.WEATHER_LNG, "-122.3411561");
        widget.initOption(WeatherSettings.WEATHER_CITY, "Seattle, WA");
        widget.initOption(WeatherSettings.WEATHER_UNITS, 0); // default to fahrenheit
        widget.initOption(WeatherSettings.WEATHER_TYPE, 1); // default to weekly forecast
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lat", widget.getOption(WeatherSettings.WEATHER_LAT).value);
        json.put("lng", widget.getOption(WeatherSettings.WEATHER_LNG).value);
        json.put("units", widget.loadOrInitOption(WeatherSettings.WEATHER_UNITS, context).value);
        json.put(WeatherSettings.WEATHER_TYPE, widget.loadOrInitOption(WeatherSettings.WEATHER_TYPE, context).value);
        return json;
    }

    @Override
    public WidgetSettingsFragment createSettingsFragment() {
        return new WeatherSettings();
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return widget.loadOrInitOption(WeatherSettings.WEATHER_CITY, context).value;
    }
}
