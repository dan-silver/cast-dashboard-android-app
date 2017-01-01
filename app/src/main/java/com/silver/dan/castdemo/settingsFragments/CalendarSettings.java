package com.silver.dan.castdemo.settingsFragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.CalendarInfo;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.widgets.CalendarWidget;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalendarSettings extends WidgetSettingsFragment {

    public static String ALL_CALENDARS = "ALL_CALENDARS";
    public static String CALENDAR_ENABLED = "CALENDAR_ENABLED";
    public static String SHOW_EVENT_LOCATIONS = "SHOW_EVENT_LOCATIONS";
    public static String SHOW_EVENTS_UNTIL = "SHOW_EVENTS_UNTIL";

    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 1000; // this integer must be unique across all app permission requests

    Integer numDaysDisplayValues[] = new Integer[]{3, 7, 14, 30, 90};

    WidgetOption optionAllCalendars;
    WidgetOption optionShowEventLocations;
    WidgetOption optionShowEventsUntil;

    @BindView(R.id.display_all_calendars)
    Switch allCalendars;

    @BindView(R.id.calendar_list)
    RecyclerView calendarList;

    @BindView(R.id.display_event_locations)
    Switch eventLocations;

    @BindView(R.id.show_events_until)
    TwoLineSettingItem showEventsUntil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.calendar_settings, container, false);
        ButterKnife.bind(this, view);

        // restore saved options into GUI

        optionAllCalendars = loadOrInitOption(CalendarSettings.ALL_CALENDARS);
        optionShowEventLocations = loadOrInitOption(CalendarSettings.SHOW_EVENT_LOCATIONS);
        optionShowEventsUntil = loadOrInitOption(CalendarSettings.SHOW_EVENTS_UNTIL);


        supportWidgetHeightOption();
        supportWidgetScrollInterval();


        allCalendars.setChecked(optionAllCalendars.getBooleanValue());

        allCalendars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionAllCalendars.update(isChecked);
                updateCalendarListContents();
                refreshWidget();
            }
        });

        eventLocations.setChecked(optionShowEventLocations.getBooleanValue());
        eventLocations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionShowEventLocations.update(isChecked);
                updateWidgetProperty(SHOW_EVENT_LOCATIONS, optionShowEventLocations.getBooleanValue());
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        calendarList.setLayoutManager(mLayoutManager);

        updateCalendarListVisibility();

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {
            updateCalendarListContents();
        }

        updateCalendarUntilTextView();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateCalendarListContents();
                    updateCalendarUntilTextView();
                } else {
                    Context context = getContext();
                    CharSequence text = "The calendar widget cannot work without the read calendar permission.  Either delete the widget or allow access.";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        }
    }

    @OnClick(R.id.show_events_until)
    public void showEventsUntilCallback() {
        new MaterialDialog.Builder(getContext())
                .title("Calendar Duration")
                .items(R.array.calendar_duration_list)
                .itemsCallbackSingleChoice(getSelectedCalendarOptionIndex(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        optionShowEventsUntil.update(numDaysDisplayValues[which]);
                        updateCalendarUntilTextView();
                        refreshWidget();
                        return true;
                    }
                })
                .show();
    }

    private void updateCalendarUntilTextView() {
        showEventsUntil.setSubHeaderText(getResources().getStringArray(R.array.calendar_duration_list)[getSelectedCalendarOptionIndex()]);
    }

    public void updateCalendarListContents() {
        updateCalendarListVisibility();
        // query for the list of calendars
        List<CalendarInfo> calendars = CalendarWidget.getCalendars(getContext(), widget);
        CalendarListAdapter mAdapter = new CalendarListAdapter(calendars, widget);
        calendarList.setAdapter(mAdapter);
    }

    public void updateCalendarListVisibility() {
        if (optionAllCalendars.getBooleanValue()) {
            calendarList.setVisibility(View.GONE);
        } else {
            calendarList.setVisibility(View.VISIBLE);
        }

    }

    private int getSelectedCalendarOptionIndex() {
        return Arrays.asList(numDaysDisplayValues).indexOf(optionShowEventsUntil.getIntValue());
    }

    public static List<WidgetOption> getEnabledCalendars(Widget widget) {
        return widget.getOptions(CalendarSettings.CALENDAR_ENABLED);
    }
}