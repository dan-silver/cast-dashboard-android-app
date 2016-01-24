package com.example.dan.castdemo.widgets;

import com.example.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dan on 1/24/2016.
 */
abstract public class UIWidget {
    public Widget widget;

    public abstract JSONObject getContent() throws JSONException;
}
