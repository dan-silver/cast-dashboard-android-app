package com.example.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CalendarSettings extends Fragment {

    public static String ALL_CALENDARS = "ALL_CALENDARS";
    public static String ALL_CALENDARS_TRUE = "ALL_CALENDARS_TRUE";
    public static String ALL_CALENDARS_FALSE = "ALL_CALENDARS_FALSE";

    private Widget widget;

    @Bind(R.id.display_all_calendars)
    android.support.v7.widget.SwitchCompat allCalendars;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        long widgetId = bundle.getLong(Widget.ID, -1);

        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_settings, container, false);
        ButterKnife.bind(this, view);

        // restore saved options into GUI

        final WidgetOption optionAllCalendars = widget.getOption(CalendarSettings.ALL_CALENDARS);
        allCalendars.setChecked(optionAllCalendars.value.equals(ALL_CALENDARS_TRUE));


        allCalendars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    optionAllCalendars.value = isChecked ? ALL_CALENDARS_TRUE : ALL_CALENDARS_FALSE;
                    optionAllCalendars.save();
                }
            }
        );

        return view;
    }

    public static void init(Widget widget) {
        WidgetOption allCalendars = new WidgetOption();


        allCalendars.key = ALL_CALENDARS;
        allCalendars.value = ALL_CALENDARS_TRUE;
        allCalendars.associateWidget(widget);
        allCalendars.save();

    }

}
