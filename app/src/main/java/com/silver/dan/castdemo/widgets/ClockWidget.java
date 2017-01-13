package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.ClockSettings;
import com.silver.dan.castdemo.settingsFragments.CountdownSettings;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class ClockWidget extends UIWidget {

    public ClockWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        widget.initOption(ClockSettings.SHOW_SECONDS, false);
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject options = new JSONObject();
        options.put(ClockSettings.SHOW_SECONDS, widget.loadOrInitOption(ClockSettings.SHOW_SECONDS, context).getBooleanValue());
        return options;
    }

    @Override
    public WidgetSettingsFragment createSettingsFragment() {
        return new ClockSettings();
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return "Clock Widget";
    }
}
