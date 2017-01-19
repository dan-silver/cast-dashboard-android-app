package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.settingsFragments.IFrameSettings;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class IFrameWidget extends UIWidget {

    public IFrameWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        widget.initOption(IFrameSettings.URL, "http://cnn.com");
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject options = new JSONObject();
        options.put(IFrameSettings.URL, widget.loadOrInitOption(IFrameSettings.URL, context).value);
        return options;
    }

    @Override
    public WidgetSettingsFragment createSettingsFragment() {
        return new IFrameSettings();
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        WidgetOption urlOption = widget.loadOrInitOption(IFrameSettings.URL, context);

        if (urlOption.value.equals("")) {
            return "URL not set";
        }

        return urlOption.value;
    }
}
