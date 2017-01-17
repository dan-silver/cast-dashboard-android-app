package com.silver.dan.castdemo.settingsFragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.AuthHelper;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.SimpleCallback;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.widgets.GoogleCalendarWidget;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoogleCalendarSettings extends WidgetSettingsFragment {

    public static String GOOGLE_CALENDARS_ENABLED = "GOOGLE_CALENDARS_ENABLED";
    public static String GOOGLE_SHOW_EVENT_LOCATIONS = "GOOGLE_SHOW_EVENT_LOCATIONS";
    public static String GOOGLE_SHOW_EVENTS_UNTIL = "GOOGLE_SHOW_EVENTS_UNTIL";

    public static final int PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR = 2000; // this integer must be unique across all app permission requests

    Integer numDaysDisplayValues[] = new Integer[]{3, 7, 14, 30, 90};

    WidgetOption optionShowEventLocations;
    WidgetOption optionShowEventsUntil;
    WidgetOption optionCalendarsEnabled;

    @BindView(R.id.calendar_list)
    RecyclerView calendarList;

    @BindView(R.id.display_event_locations)
    Switch eventLocations;

    @BindView(R.id.show_events_until)
    TwoLineSettingItem showEventsUntil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.google_calendar_settings, container, false);
        ButterKnife.bind(this, view);

        initView();

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_GOOGLE_CALENDAR: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // @todo
//                    updateCalendarListContents();
//                    updateCalendarUntilTextView();
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
        // query for the list of calendars
        AuthHelper.getGoogleAccessToken(getContext(), new SimpleCallback<String>() {
            @Override
            public void onComplete(String accessToken) {
                GoogleCalendarWidget calendarWidget = (GoogleCalendarWidget) widget.getUIWidget(getContext());
                calendarWidget.getCalendars(accessToken, new SimpleCallback<List<GoogleCalendarWidget.Calendar>>() {
                    @Override
                    public void onComplete(List<GoogleCalendarWidget.Calendar> calendars) {

                        GoogleCalendarListAdapter mAdapter = new GoogleCalendarListAdapter(calendars, widget);
                        calendarList.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


            }

            @Override
            public void onError(Exception e) {

            }
        });
    }


    private int getSelectedCalendarOptionIndex() {
        return Arrays.asList(numDaysDisplayValues).indexOf(optionShowEventsUntil.getIntValue());
    }

    @Override
    public void initView() {

        // restore saved options into GUI
        optionShowEventLocations = loadOrInitOption(GoogleCalendarSettings.GOOGLE_SHOW_EVENT_LOCATIONS);
        optionShowEventsUntil = loadOrInitOption(GoogleCalendarSettings.GOOGLE_SHOW_EVENTS_UNTIL);
        optionCalendarsEnabled = loadOrInitOption(GoogleCalendarSettings.GOOGLE_CALENDARS_ENABLED);


        supportWidgetHeightOption();
        supportWidgetScrollInterval();
        supportWidgetRefreshInterval();

        eventLocations.setChecked(optionShowEventLocations.getBooleanValue());
        eventLocations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionShowEventLocations.update(isChecked);
                updateWidgetProperty(GOOGLE_SHOW_EVENT_LOCATIONS, optionShowEventLocations.getBooleanValue());
            }
        });

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        calendarList.setLayoutManager(mLayoutManager);

        updateCalendarListContents();
        updateCalendarUntilTextView();


    }
}