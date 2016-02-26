package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;

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

        showSecondsOption = loadOrInitOption(ClockSettings.SHOW_SECONDS);

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


}
