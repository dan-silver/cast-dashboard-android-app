package com.silver.dan.castdemo;

import android.content.Context;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CastCommunicator {
    private static DataCastManager mCastManager;
    private static Dashboard dashboard;
    private static String namespace;

    public static void init(DataCastManager mCastManager, Dashboard dashboard, String namespace) {
        CastCommunicator.mCastManager = mCastManager;
        CastCommunicator.dashboard = dashboard;
        CastCommunicator.namespace = namespace;
    }

    public static void sendWidgetProperty(Widget widget, String property, Object value) {
        try {
            JSONObject propertyValue = new JSONObject();
            propertyValue.put("widgetId", widget.guid);
            propertyValue.put("property", property);
            propertyValue.put("value", value);
            CastCommunicator.sendJSON("widgetProperty", propertyValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static void deleteWidget(Widget widget) {
        try {
            JSONObject info = new JSONObject();
            info.put("id", widget.guid);
            CastCommunicator.sendJSON("deleteWidget", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    static void sendJSON(final String key, final JSONObject payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainer(container);
    }

    private static void sendJSON(final String key, final JSONArray payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CastCommunicator.sendJSONContainer(container);
    }

    private static void sendJSONContainer(final JSONObject container) {
        if (!mCastManager.isConnected())
            return;

        new Runnable() {
            public void run() {
                try {
                    mCastManager.sendDataMessage(container.toString(), CastCommunicator.namespace);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    public static void sendWidget(Widget widget, Context context) {
        if (!mCastManager.isConnected())
            return;

        JSONArray widgetsArr = new JSONArray();
        widgetsArr.put(widget.getJSONContent(context));
        CastCommunicator.sendWidgets(widgetsArr);
    }

    static void sendAllWidgets(Context context) {
        if (!mCastManager.isConnected())
            return;

        if (CastCommunicator.dashboard == null) {
            throw new Error("dashboard is not set yet");
        }

        JSONArray widgetsArr = new JSONArray();
        for (Widget widget : CastCommunicator.dashboard.widgets) {
            widgetsArr.put(widget.getJSONContent(context));
        }
        CastCommunicator.sendJSON("allWidgets", widgetsArr);
    }

    private static void sendWidgets(JSONArray widgetsArr) {
        CastCommunicator.sendJSON("widgets", widgetsArr);
    }

}