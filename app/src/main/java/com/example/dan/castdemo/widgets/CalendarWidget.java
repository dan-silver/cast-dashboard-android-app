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
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.settingsFragments.CalendarSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class CalendarWidget extends UIWidget {

    public static String HUMAN_NAME = "Calendar";


    // Projection array. Creating indices for this array instead of doing

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

    public CalendarWidget(Context context, Widget widget) {
        super(context, widget);
    }


    public static List<CalendarInfo> getCalendars(Context context, Widget widget) {
        // Run query
        Cursor cur;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new ArrayList<>();
        }
        cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

        if (cur == null)
            return new ArrayList<>();


        ArrayList<CalendarInfo> calendars = new ArrayList<>();
        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // Do something with the values...
            CalendarInfo calendarInfo = new CalendarInfo();
            calendarInfo.name = displayName;
//            calendarInfo.id = withAppendedId(uri, calID).toString();
            calendarInfo.id = Long.toString(calID);


            calendars.add(calendarInfo);
        }
        cur.close();

        // figure out which of the calendars are enabled

        List<WidgetOption> enabled_calendars = CalendarSettings.getEnabledCalendars(widget);


        for (CalendarInfo calendar : calendars) {
            calendar.enabled = false;
            for (WidgetOption enabledCalendar : enabled_calendars) {
                if (calendar.id.equals(enabledCalendar.value)) {
                    calendar.enabled = true;
                    break;
                }
            }
        }

        return calendars;
    }

    public JSONArray getCalendarEvents(Context context, List<String> calendarIds, boolean allCalendars) throws JSONException {

        Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        // starting time in milliseconds
        long begin = cal1.getTimeInMillis();

        // end time
        cal1.add(Calendar.MONTH, 2);
        long end = cal1.getTimeInMillis(); // ending time in milliseconds

        String[] projection =
                new String[]{
                        CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.TITLE,
                        CalendarContract.Instances.DISPLAY_COLOR,
                        CalendarContract.Instances.ALL_DAY,
                        CalendarContract.Instances.EVENT_LOCATION};

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


        List<String> selection = new ArrayList<>();
        List<String> selectionArgs = new ArrayList<>();
        String selectionStr = "";
        if (!allCalendars) {
            for (String calendarId : calendarIds) {
                selection.add("(" + CalendarContract.Instances.CALENDAR_ID + " = ?)");
                selectionArgs.add(calendarId);
            }
            selectionStr = "(" + android.text.TextUtils.join(" OR ", selection) + ")";
        }

        Cursor cur =
                context.getContentResolver().query(
                        Uri.parse(CalendarContract.Instances.CONTENT_URI + "/" + begin + "/" + end),
                        projection,
                        selectionStr,
                        selectionArgs.toArray(new String[selectionArgs.size()]),
                        null);

        JSONArray events = new JSONArray();

        while (cur.moveToNext() && events.length() < 50) {
            long startDate = cur.getLong(0);
            long endDate = cur.getLong(1);
            String title = cur.getString(2);
            int color = cur.getInt(3);
            boolean allDay = cur.getInt(4) == 1;
            String location = cur.getString(5);

            JSONObject event = new JSONObject();
            event.put("title", title);
            event.put("color", Integer.toHexString(color).substring(2));
            event.put("start", startDate);
            event.put("end", endDate);
            event.put("allDay", allDay);
            event.put("locationStr", location);

            events.put(event);


        }
        cur.close();
        return events;
    }

    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();


        List<String> calendarIds = new ArrayList<>();

        WidgetOption optionAllCalendars = widget.getOption(CalendarSettings.ALL_CALENDARS);
        boolean showAllCalendars = optionAllCalendars.value.equals(CalendarSettings.ALL_CALENDARS_TRUE);

        if (!showAllCalendars) {
            for (WidgetOption a : CalendarSettings.getEnabledCalendars(widget)) {
                calendarIds.add(a.value);
            }
        }


        json.put("events", getCalendarEvents(context, calendarIds, showAllCalendars));
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        WidgetOption optionAllCalendars = widget.getOption(CalendarSettings.ALL_CALENDARS);
        if (optionAllCalendars.value.equals(CalendarSettings.ALL_CALENDARS_TRUE)) {
            return "Displaying all calendars";
        } else {
            //@todo optimize this section

            // contains ids
            List<WidgetOption> enabledCalendars = CalendarSettings.getEnabledCalendars(widget);


            // contains title, id
            List<CalendarInfo> calendars = getCalendars(context, widget);


            int numCalendars = enabledCalendars.size();

            if (numCalendars == 0) {
                return "No calendars selected";
            }

            ArrayList<String> previewCalendars = new ArrayList<>();
            int charCount = 0;
            for (CalendarInfo calendarInfo : calendars) {
                for (WidgetOption option : enabledCalendars) {
                    if (calendarInfo.id.equals(option.value)) {
                        previewCalendars.add(calendarInfo.name);
                        charCount += calendarInfo.name.length();
                        break;

                    }
                }
                if (charCount > 20) {
                    break;
                }
            }

            int notShownCalendars = numCalendars - previewCalendars.size();

            // all calendar names previewed
            if (notShownCalendars == 0) {
                return android.text.TextUtils.join(", ", previewCalendars);
            }

            // overflow case
            return android.text.TextUtils.join(", ", previewCalendars) + " and " + notShownCalendars + " more";
        }
    }
}