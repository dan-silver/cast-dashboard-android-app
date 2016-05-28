package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.FreeTextSetting;
import com.silver.dan.castdemo.settingsFragments.RSSSettings;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dan on 5/28/16.
 */
public class FreeTextWidget extends UIWidget {

        public FreeTextWidget(Context context, Widget widget) {
            super(context, widget);
        }

        @Override
        public void init() {
            widget.initOption(FreeTextSetting.CUSTOM_TEXT, "");
        }


        @Override
        public JSONObject getContent() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(FreeTextSetting.CUSTOM_TEXT, widget.loadOrInitOption(FreeTextSetting.CUSTOM_TEXT, context).value);
            return json;
        }

        @Override
        public String getWidgetPreviewSecondaryHeader() {
            return "----";
        }
    }
