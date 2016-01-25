package com.example.dan.castdemo.widgets;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class UIWidget {
    public Widget widget;

    public abstract JSONObject getContent() throws JSONException;

    public abstract String getWidgetPreviewSecondaryHeader();
}
