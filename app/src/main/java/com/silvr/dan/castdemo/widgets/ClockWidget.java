package com.silvr.dan.castdemo.widgets;

import android.content.Context;

import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.settingsFragments.ClockSettings;

import org.json.JSONException;
import org.json.JSONObject;


public class ClockWidget extends UIWidget {
    public static String HUMAN_NAME = "Clock";

    public ClockWidget(Context context, Widget widget) {
        super(context, widget);
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject options = new JSONObject();
        options.put("show_seconds", widget.getOption(ClockSettings.SHOW_SECONDS).getBooleanValue());
        return options;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return "Clock Widget";
    }
}
