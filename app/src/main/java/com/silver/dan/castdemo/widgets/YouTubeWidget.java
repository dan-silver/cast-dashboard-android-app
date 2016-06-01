package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;
import com.silver.dan.castdemo.settingsFragments.YouTubeSettings;

import org.json.JSONException;
import org.json.JSONObject;

public class YouTubeWidget extends UIWidget {

    public YouTubeWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        widget.initOption(YouTubeSettings.PLAYLIST_DETAILS, "[]");
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(YouTubeSettings.PLAYLIST_DETAILS, widget.loadOrInitOption(YouTubeSettings.PLAYLIST_DETAILS, context).value);
        return json;
    }

    @Override
    public WidgetSettingsFragment createSettingsFragment() {
        return new YouTubeSettings();
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        return "Playing/repeating video ___________.";
    }
}
