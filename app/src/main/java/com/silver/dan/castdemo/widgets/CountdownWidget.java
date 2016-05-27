package com.silver.dan.castdemo.widgets;

import android.content.Context;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.settingsFragments.ClockSettings;
import com.silver.dan.castdemo.settingsFragments.CountdownSettings;
import com.silver.dan.castdemo.settingsFragments.RSSSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CountdownWidget extends UIWidget {

    public CountdownWidget(Context context, Widget widget) {
        super(context, widget);
    }

    @Override
    public void init() {
        Date date = new Date();
        long oneWeek = date.getTime() + (long) 7*24*60*60*1000;
        widget.initOption(CountdownSettings.COUNTDOWN_DATE, oneWeek);
        widget.initOption(CountdownSettings.COUNTDOWN_TEXT, context.getString(R.string.a_wonderful_day));
    }


    @Override
    public JSONObject getContent() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(CountdownSettings.COUNTDOWN_DATE, widget.loadOrInitOption(CountdownSettings.COUNTDOWN_DATE, context).value);
        json.put(CountdownSettings.COUNTDOWN_TEXT, widget.loadOrInitOption(CountdownSettings.COUNTDOWN_TEXT, context).value);
        return json;
    }

    @Override
    public String getWidgetPreviewSecondaryHeader() {
        Date countdownTarget = widget.loadOrInitOption(CountdownSettings.COUNTDOWN_DATE, context).getDate();


        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        String dateString = dateFormat.format(countdownTarget);
        String timeString = timeFormat.format(countdownTarget);

        String title = widget.loadOrInitOption(CountdownSettings.COUNTDOWN_TEXT, context).value;

        return "Counting down to " + dateString + " " + timeString + " for " + title;
    }
}
