package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Settings.MapType;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.settingsFragments.MapSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MapWidget extends UIWidget {

    public MapWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {

        //https://www.google.com/maps/@47.6061734,-122.3310611,16.04z
        widget.initOption(MapSettings.LOCATION_LAT, "47.6061734");
        widget.initOption(MapSettings.LOCATION_LONG, "-122.3310611");
//        widget.initOption(MapSettings.LOCATION_NAME, "Seattle, Washington");
        widget.initOption(MapSettings.LOCATION_ADDRESS, "Seattle, Washington");
        widget.initOption(MapSettings.MAP_ZOOM, 10);
        widget.initOption(MapSettings.SHOW_TRAFFIC, false);
        widget.initOption(MapSettings.MAP_TYPE, MapType.ROADMAP.getValue());
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(MapSettings.LOCATION_LAT, widget.getOption(MapSettings.LOCATION_LAT).value);
        json.put(MapSettings.LOCATION_LONG, widget.getOption(MapSettings.LOCATION_LONG).value);
        json.put(MapSettings.MAP_ZOOM, widget.getOption(MapSettings.MAP_ZOOM).getIntValue());
        json.put(MapSettings.SHOW_TRAFFIC, widget.getOption(MapSettings.SHOW_TRAFFIC).getBooleanValue());
        json.put(MapSettings.MAP_TYPE, MapType.getMapType(widget.loadOrInitOption(MapSettings.MAP_TYPE, context).getIntValue()).toString());
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        WidgetOption locationAddress = widget.getOption(MapSettings.LOCATION_ADDRESS);
        if (locationAddress != null) {
            return widget.getOption(MapSettings.LOCATION_ADDRESS).value;
        }

        return "Location not set";
    }
}
