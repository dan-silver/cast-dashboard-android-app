package com.silvr.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.silvr.dan.castdemo.CalendarInfo;
import com.silvr.dan.castdemo.MainActivity;
import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.silvr.dan.castdemo.widgets.CalendarWidget;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalendarSettings extends WidgetSettingsFragment {

    public static String ALL_CALENDARS = "ALL_CALENDARS";
    public static String CALENDAR_ENABLED = "CALENDAR_ENABLED";
    public static String SHOW_EVENT_LOCATIONS = "SHOW_EVENT_LOCATIONS";

    WidgetOption optionAllCalendars;
    WidgetOption optionShowEventLocations;

    @Bind(R.id.display_all_calendars)
    Switch allCalendars;

    @Bind(R.id.calendar_list)
    RecyclerView calendarList;


    @Bind(R.id.display_event_locations)
    Switch eventLocations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_settings, container, false);
        ButterKnife.bind(this, view);

        // restore saved options into GUI

        optionAllCalendars = loadOrInitOption(CalendarSettings.ALL_CALENDARS);
        optionShowEventLocations = loadOrInitOption(CalendarSettings.SHOW_EVENT_LOCATIONS);

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