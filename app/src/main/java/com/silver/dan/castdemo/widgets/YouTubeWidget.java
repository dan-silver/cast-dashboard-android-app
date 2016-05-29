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
        widget.initOption(YouTubeSettings.VIDEO_ID, -1);
        widget.initOption(YouTubeSettings.CACHED_VIDEO_NAME, "");
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(YouTubeSettings.VIDEO_ID, widget.loadOrInitOption(YouTubeSettings.VIDEO_ID, context).value);
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
