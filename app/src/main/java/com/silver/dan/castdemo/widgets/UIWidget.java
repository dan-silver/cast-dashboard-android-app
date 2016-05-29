package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class UIWidget {
    public Widget widget;
    public Context context;

    public CanBeCreatedListener canBeCreatedListener;

    public abstract JSONObject getContent() throws JSONException;

    public int requestPermissions() {
        return -1;
    }

    public abstract WidgetSettingsFragment createSettingsFragment();


    /*
     * This method should return true if there are no prerequisites to the widget being created.
     * Return false if the widget needs special permissions before it's created.
     */
    public boolean canBeCreated() {
        return true;
    }

    public abstract String getWidgetPreviewSecondaryHeader();

    public UIWidget(Context context, Widget widget) {
        this.widget = widget;
        this.context = context;
    }

    public abstract void init();

    public void setOnCanBeCreatedListener(CanBeCreatedListener listener) {
        this.canBeCreatedListener = listener;
        if (canBeCreated()) {
            listener.onCanBeCreated();
        } else {
            // request the permissions for the widget, and set the callback key
            int requirementsToCreateWidget = requestPermissions();
            this.canBeCreatedListener.setRequiredCondition(requirementsToCreateWidget);
        }
    }
}
