package com.silver.dan.castdemo.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.silver.dan.castdemo.AuthHelper;
import com.silver.dan.castdemo.SimpleCallback;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.GoogleCalendarSettings;
import com.silver.dan.castdemo.settingsFragments.WidgetSettingsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by dan on 5/28/16.
 */
public class GoogleCalendarWidget extends UIWidget {

    public static String RequiredScope = "https://www.googleapis.com/auth/calendar.readonly";

    public GoogleCalendarWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public int requestPermissions(Activity activity) {
        AuthHelper authHelper = new AuthHelper(context);

        Set<Scope> scopes = new HashSet<>();
        scopes.addAll(AuthHelper.grantedScopes);
        scopes.add(new Scope(RequiredScope));

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, authHelper.getGoogleGSO(scopes))
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR);

        return GoogleCalendarSettings.PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR;
    }


    public boolean canBeCreated() {
        return AuthHelper.grantedScopes.contains(new Scope(RequiredScope));
    }

    @Override
    public void init() {
        widget.initOption(GoogleCalendarSettings.GOOGLE_SHOW_EVENT_LOCATIONS, true);
        widget.initOption(GoogleCalendarSettings.GOOGLE_SHOW_EVENTS_UNTIL, 30);
        widget.initOption(GoogleCalendarSettings.GOOGLE_CALENDARS_ENABLED, "");
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("CALENDAR_IDS", getEnabledCalendars());
        return json;
    }

    public class Calendar {
        public boolean enabled;
        public String summary;
        public String timeZone;
        public String id;
        public String backgroundColor;
        public String foregroundColor;
        public String accessRole;
//        "kind": "calendar#calendarListEntry",
//                "etag": "\"1462156477999000\"",
//                "id": "n654t8kcu32etu6lo9osnhkh24@group.calendar.google.com",
//                "summary": "Fake Events 3",
//                "timeZone": "America/Los_Angeles",
//                "colorId": "9",
//                "backgroundColor": "#7bd148",
//                "foregroundColor": "#000000",
//                "accessRole": "owner",
//                "defaultReminders": []
//    },
    }

    private List<String> getEnabledCalendars() {
        return widget.loadOrInitOption(GoogleCalendarSettings.GOOGLE_CALENDARS_ENABLED, context).getList();
    }

    @Override
    public WidgetSettingsFragment createSettingsFragment() {
        return new GoogleCalendarSettings();
    }

    public void getCalendars(String accessToken, final SimpleCallback<List<Calendar>> callback) {
        Ion.with(context)
                .load("https://www.googleapis.com/calendar/v3/users/me/calendarList")
                .setHeader("Authorization", "Bearer " + accessToken)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            callback.onError(e);
                            return;
                        }
                        JsonArray calendars = result.get("items").getAsJsonArray();

                        List<String> enabledCalendars = getEnabledCalendars();

                        List<Calendar> calendars1 = new ArrayList<>();
                        for (int i=0; i<calendars.size(); i++) {
                            JsonObject rawCal = calendars.get(i).getAsJsonObject();
                            Calendar cal = new Calendar();

                            cal.summary = rawCal.get("summary").getAsString();
                            cal.timeZone = rawCal.get("timeZone").getAsString();
                            cal.id = rawCal.get("id").getAsString();
                            cal.backgroundColor = rawCal.get("backgroundColor").getAsString();
                            cal.foregroundColor = rawCal.get("foregroundColor").getAsString();
                            cal.accessRole = rawCal.get("accessRole").getAsString();
                            cal.enabled = enabledCalendars.contains(cal.id);

                            calendars1.add(cal);

                        }

                        callback.onComplete(calendars1);
                    }
                });
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        List<String> enabledCalendars = getEnabledCalendars();
        if (enabledCalendars.size() == 0) {
            return "No calendars selected";
        } else if (enabledCalendars.size() == 1) {
            return "Displaying events from one calendar";
        }



        return "Displaying events from " + enabledCalendars.size() + " calendars";
    }
}