package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.settingsFragments.MapSettings;

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
        json.put("lat", widget.getOption(MapSettings.LOCATION_LAT).value);
        json.put("lng", widget.getOption(MapSettings.LOCATION_LONG).value);
        json.put("zoom", Integer.valueOf(widget.getOption(MapSettings.MAP_ZOOM).value));
        json.put("traffic", widget.getOption(MapSettings.SHOW_TRAFFIC).getBooleanValue());
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        WidgetOption locationName = widget.getOption(MapSettings.LOCATION_NAME);
        if (locationName != null) {
            return widget.getOption(MapSettings.LOCATION_NAME).value;
        }

        WidgetOption locationAddress = widget.getOption(MapSettings.LOCATION_NAME);
        if (locationAddress != null) {
            return widget.getOption(MapSettings.LOCATION_ADDRESS).value;
        }

        return "Unknown Location";
    }
}
