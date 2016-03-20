package com.silver.dan.castdemo;

import android.content.Context;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import hotchemi.android.rate.AppRate;

public class CastCommunicator {

    static Context context;
    private static DataCastManager mCastManager;

    public static void init(Context ctx, DataCastManager mCastManager) {
        CastCommunicator.context = ctx;
        CastCommunicator.mCastManager = mCastManager;
    }

    public static void sendWidget(Widget widget) {
        try {
            CastCommunicator.sendJSON("widget", widget.getJSONContent(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendWidgetProperty(Widget widget, String property, Object value) {
        try {
            JSONObject propertyValue = new JSONObject();
            propertyValue.put("widgetId", widget.id);
            propertyValue.put("property", property);
            propertyValue.put("value", value);
            CastCommunicator.sendJSON("widgetProperty", propertyValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deleteWidget(Widget widget) {
        try {
            JSONObject info = new JSONObject();
            info.put("id", widget.id);
            CastCommunicator.sendJSON("deleteWidget", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void sendJSON(final String key, final JSONObject payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainer(container);
    }

    public static void sendJSON(final String key, final JSONArray payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CastCommunicator.sendJSONContainer(container);
    }

    public static void sendJSONContainer(final JSONObject container) {
        new Runnable() {
            public void run() {
                try {
                    mCastManager.sendDataMessage(container.toString(), context.getResources().getString(R.string.namespace));
                    AppRate.passSignificantEvent((MainActivity) context);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.run();
    }
}
