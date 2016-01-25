package com.example.dan.castdemo.widgets;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.example.dan.castdemo.CalendarInfo;
import com.example.dan.castdemo.Widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CalendarWidget extends UIWidget {

    public static String HUMAN_NAME = "Calendar";

    private final Context context;

    public CalendarWidget(Context context, Widget widget) {
        this.context = context;
        this.widget = widget;
    }

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
            CalendarContract.Calendars.CALENDAR_COLOR                 // 4
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private static final int PROJECTION_CALENDAR_COLOR = 4;


    public static List<CalendarInfo> getCalendars(Context context) {
        // Run query
        Cursor cur;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{"com.google"};
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        if (cur == null)
            return null;


        ArrayList<CalendarInfo> calendars = new ArrayList<>();
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;
            int color;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            color = cur.getInt(PROJECTION_CALENDAR_COLOR);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // Do something with the values...
            CalendarInfo calendarInfo = new CalendarInfo();
            calendarInfo.name = displayName;
            calendarInfo.id = calID;


            calendars.add(calendarInfo);
        }
        cur.close();
        return calendars;
    }

    // return pure JSON for frontend?
    public JSONArray getCalendarEvents(Context context, List<CalendarInfo> calendars) throws JSONException {
        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        long begin = cal1.getTimeInMillis();
        // starting time in milliseconds
        long end = begin + 604800000; // ending time in milliseconds
        String[] projection =
                new String[]{
                        CalendarContract.Instances._ID,
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.EVENT_ID,
                        CalendarContract.Instances.TITLE};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Cursor cur =
                CalendarContract.Instances.query(
                        context.getContentResolver(),
                        projection,
                        begin,
                        end);

        JSONArray events = new JSONArray();
        while (cur.moveToNext()) {
            String eventID = cur.getString(3);
            String title = cur.getString(4);
            String _id = cur.getString(0);
            JSONObject event = new JSONObject();

            event.put("title", title);

            events.put(event);


        }
        cur.close();
        return events;
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("events", getCalendarEvents(context, null));
        json.put("testkey1", "testvalue1");
        return json;
    }
}