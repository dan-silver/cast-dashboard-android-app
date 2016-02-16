package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.silver.dan.castdemo.CalendarInfo;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.widgets.CalendarWidget;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalendarSettings extends WidgetSettingsFragment {

    public static String ALL_CALENDARS = "ALL_CALENDARS";
    public static String CALENDAR_ENABLED = "CALENDAR_ENABLED";

    WidgetOption optionAllCalendars;

    @Bind(R.id.display_all_calendars)
    android.support.v7.widget.SwitchCompat allCalendars;

    @Bind(R.id.calendar_list)
    RecyclerView calendarList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_settings, container, false);
        ButterKnife.bind(this, view);

        // restore saved options into GUI

        optionAllCalendars = widget.getOption(CalendarSettings.ALL_CALENDARS);
        allCalendars.setChecked(optionAllCalendars.getBooleanValue());

        allCalendars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionAllCalendars.setBooleanValue(isChecked);
                displayCalendarList();
                optionAllCalendars.save();
                refreshWidget();

            }
        }
        );

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        calendarList.setLayoutManager(mLayoutManager);

        displayCalendarList();
        return view;
    }

    public void displayCalendarList() {
        if (optionAllCalendars.getBooleanValue()) {
            calendarList.setVisibility(View.INVISIBLE);
        } else {
            calendarList.setVisibility(View.VISIBLE);

            // query for the list of calendars
            List<CalendarInfo> calendars = CalendarWidget.getCalendars(getContext(), widget);
            CalendarListAdapter mAdapter = new CalendarListAdapter(calendars, widget);
            calendarList.setAdapter(mAdapter);
        }
    }

    public static void init(Widget widget) {
        widget.initOption(ALL_CALENDARS, true);
    }

    public static List<WidgetOption> getEnabledCalendars(Widget widget) {
        return widget.getOptions(CalendarSettings.CALENDAR_ENABLED);
    }
}