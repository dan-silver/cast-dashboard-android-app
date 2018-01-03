package com.silver.dan.castdemo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CastCommunicator {
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
        new Runnable() {
            public void run() {
                if (MainActivity.mHelloWorldChannel != null) {
                    MainActivity.mHelloWorldChannel.sendMessage(MainActivity.mCastSession, container.toString());
                }
            }
        }.run();
    }

    public static void sendWidget(Widget widget, Context context) {
        JSONArray widgetsArr = new JSONArray();
        widgetsArr.put(widget.getJSONContent(context));
        CastCommunicator.sendWidgets(widgetsArr);
    }

    static void sendAllWidgets(Context context, Dashboard dashboard) {
        JSONArray widgetsArr = new JSONArray();
        for (Widget widget : dashboard.getWidgetList()) {
            widgetsArr.put(widget.getJSONContent(context));
        }
        CastCommunicator.sendJSON("allWidgets", widgetsArr);
    }

    private static void sendWidgets(JSONArray widgetsArr) {
        CastCommunicator.sendJSON("widgets", widgetsArr);
    }
}