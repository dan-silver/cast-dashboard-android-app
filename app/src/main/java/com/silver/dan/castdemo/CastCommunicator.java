package com.silver.dan.castdemo;

import android.content.Context;

import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class CastCommunicator {

    static Context context;
    static DataCastManager mCastManager;

    public static void init(Context ctx, DataCastManager mCastManager) {
        CastCommunicator.context = ctx;
        CastCommunicator.mCastManager = mCastManager;
    }

    public static void sendWidget(Widget widget) {
        JSONArray widgetsArr = new JSONArray();
        widgetsArr.put(widget.getJSONContent(context));
        CastCommunicator.sendWidgets(widgetsArr);
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
        if (!mCastManager.isConnected())
            return;

        new Runnable() {
            public void run() {
                try {
                    mCastManager.sendDataMessage(container.toString(), context.getResources().getString(R.string.namespace));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    public static void sendAllWidgets() {
        Widget.fetchAll(new FetchAllWidgetsListener() {
            @Override
            public void results(List<Widget> widgets) {
                JSONArray widgetsArr = new JSONArray();
                for (Widget widget : widgets) {
                    widgetsArr.put(widget.getJSONContent(context));
                }
                CastCommunicator.sendWidgets(widgetsArr);
            }
        });
    }

    public static void sendWidgets(JSONArray widgetsArr) {
        CastCommunicator.sendJSON("widgets", widgetsArr);
    }

    // for extremely large strings, don't wrap in JSON
    public static void sendText(final String text) {
        if (!mCastManager.isConnected())
            return;

        new Runnable() {
            public void run() {
                try {
                    mCastManager.sendDataMessage(text, context.getResources().getString(R.string.namespace));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }
}