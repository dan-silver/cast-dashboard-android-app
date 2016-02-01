package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class UIWidget {
    public Widget widget;
    public Context context;

    public abstract JSONObject getContent() throws JSONException;

    public abstract String getWidgetPreviewSecondaryHeader();

    public UIWidget(Context context, Widget widget) {
        this.widget = widget;
        this.context = context;
    }
}
