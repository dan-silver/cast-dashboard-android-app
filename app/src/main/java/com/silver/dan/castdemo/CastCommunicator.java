package com.silver.dan.castdemo;

import android.content.Context;

import com.google.android.gms.cast.framework.CastSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CastCommunicator {
    CastSession mCastSession;

    public CastCommunicator(CastSession session) {
        this.mCastSession = session;
    }

    public void sendWidgetProperty(Widget widget, String property, Object value) {
        try {
            JSONObject propertyValue = new JSONObject();
            propertyValue.put("widgetId", widget.guid);
            propertyValue.put("property", property);
            propertyValue.put("value", value);
            sendJSON("widgetProperty", propertyValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void deleteWidget(Widget widget) {
        try {
            JSONObject info = new JSONObject();
            info.put("id", widget.guid);
            sendJSON("deleteWidget", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void sendJSON(final String key, final JSONObject payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainer(container);
    }

    private void sendJSON(final String key, final JSONArray payload) {
        JSONObject container = new JSONObject();

        try {
            container.put(key, payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendJSONContainer(container);
    }

    private void sendJSONContainer(final JSONObject container) {
        if (MainActivity.mHelloWorldChannel != null) {
            MainActivity.mHelloWorldChannel.sendMessage(mCastSession, container.toString());
        }
    }

    public void sendWidget(Widget widget, Context context) {
        JSONArray widgetsArr = new JSONArray();
        widgetsArr.put(widget.getJSONContent(context));
        sendWidgets(widgetsArr);
    }

    void sendAllWidgets(Context context, Dashboard dashboard) {
        JSONArray widgetsArr = new JSONArray();
        for (Widget widget : dashboard.getWidgetList()) {
            widgetsArr.put(widget.getJSONContent(context));
        }
        sendJSON("allWidgets", widgetsArr);
    }

    private void sendWidgets(JSONArray widgetsArr) {
        sendJSON("widgets", widgetsArr);
    }
}