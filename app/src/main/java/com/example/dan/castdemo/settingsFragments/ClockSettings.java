package com.example.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ClockSettings extends WidgetSettingsFragment {

    @Bind(R.id.clock_show_seconds)
    Switch showSeconds;


    WidgetOption showSecondsOption;

    public static String SHOW_SECONDS = "SHOW_SECONDS";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.clock_settings, container, false);
        ButterKnife.bind(this, view);

        showSecondsOption = widget.getOption(ClockSettings.SHOW_SECONDS);

        showSeconds.setChecked(showSecondsOption.getBooleanValue());
        showSeconds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showSecondsOption.setBooleanValue(isChecked);
                showSecondsOption.save();
                refreshWidget();
            }
        });


        return view;
    }

    public static void init(Widget widget) {
        widget.initOption(SHOW_SECONDS, false);
    }
}
