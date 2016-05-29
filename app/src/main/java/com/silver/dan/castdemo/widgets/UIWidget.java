package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class UIWidget {
    public Widget widget;
    public Context context;

    public CanBeCreatedListener canBeCreatedListener;

    public abstract JSONObject getContent() throws JSONException;

    public int requestPermissions() {return -1;}


    public boolean canBeCreated() {
        return true;
    }

    public abstract String getWidgetPreviewSecondaryHeader();

    public UIWidget(Context context, Widget widget) {
        this.widget = widget;
        this.context = context;
    }

    public abstract void init();

    public void onCanBeCreated(CanBeCreatedListener listener) {
        this.canBeCreatedListener = listener;
        if (canBeCreated()) {
            listener.onCanBeCreated();
        } else {
            // request the permissions for the widget, and set the callback key
            this.canBeCreatedListener.key = requestPermissions();
        }
    }
}
