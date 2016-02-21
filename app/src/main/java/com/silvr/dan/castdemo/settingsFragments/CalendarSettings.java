package com.silvr.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.silvr.dan.castdemo.CalendarInfo;
import com.silvr.dan.castdemo.MainActivity;
import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.silvr.dan.castdemo.widgets.CalendarWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalendarSettings extends WidgetSettingsFragment {

    public static String ALL_CALENDARS = "ALL_CALENDARS";
    public static String CALENDAR_ENABLED = "CALENDAR_ENABLED";
    public static String SHOW_EVENT_LOCATIONS = "SHOW_EVENT_LOCATIONS";
    public static String SHOW_EVENTS_UNTIL= "SHOW_EVENTS_UNTIL";

    String numDaysDisplayStr[] = new String[]{"3 Days", "1 Week", "2 Weeks", "1 Month", "3 Months"};
    int numDaysDisplayValues[] = new int[]{3,7,14,30,90};


    WidgetOption optionAllCalendars;
    WidgetOption optionShowEventLocations;
    WidgetOption optionShowEventsUntil;

    @Bind(R.id.display_all_calendars)
    Switch allCalendars;

    @Bind(R.id.calendar_list)
    RecyclerView calendarList;


    @Bind(R.id.display_event_locations)
    Switch eventLocations;

    @Bind(R.id.show_events_until)
    android.support.v7.widget.AppCompatSpinner showEventsUntil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_settings, container, false);
        ButterKnife.bind(this, view);

        // restore saved options into GUI

        optionAllCalendars = loadOrInitOption(CalendarSettings.ALL_CALENDARS);
        optionShowEventLocations = loadOrInitOption(CalendarSettings.SHOW_EVENT_LOCATIONS);
        optionShowEventsUntil = loadOrInitOption(CalendarSettings.SHOW_EVENTS_UNTIL);

        allCalendars.setChecked(optionAllCalendars.getBooleanValue());

        allCalendars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionAllCalendars.setBooleanValue(isChecked);
                displayCalendarList();
                optionAllCalendars.save();
                refreshWidget();

            }
        });

        eventLocations.setChecked(optionShowEventLocations.getBooleanValue());
        eventLocations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionShowEventLocations.setBooleanValue(isChecked);
                optionShowEventLocations.save();
                refreshWidget();
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        calendarList.setLayoutManager(mLayoutManager);

        displayCalendarList();

        ArrayAdapter<String> optionAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, numDaysDisplayStr);
        showEventsUntil.setAdapter(optionAdapter);

        int position = 0;
        for (int n : numDaysDisplayValues) {
            if (n == Integer.valueOf(optionShowEventsUntil.value)) {
                break;
            }
            position++;
        }

        showEventsUntil.setSelection(position);
        showEventsUntil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                optionShowEventsUntil.setIntValue(numDaysDisplayValues[position]);
                optionShowEventsUntil.save();
                refreshWidget();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return view;
    }

    public void displayCalendarList() {
        if (optionAllCalendars.getBooleanValue()) {
            calendarList.setVisibility(View.GONE);
        } else {
            calendarList.setVisibility(View.VISIBLE);

            // query for the list of calendars
            List<CalendarInfo> calendars = CalendarWidget.getCalendars(getContext(), widget);
            CalendarListAdapter mAdapter = new CalendarListAdapter(calendars, widget);
            calendarList.setAdapter(mAdapter);
        }
    }

    public static List<WidgetOption> getEnabledCalendars(Widget widget) {
        return widget.getOptions(CalendarSettings.CALENDAR_ENABLED);
    }
}