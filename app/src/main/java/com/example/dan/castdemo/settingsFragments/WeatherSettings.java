package com.example.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.ButterKnife;

public class WeatherSettings extends WidgetSettingsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_settings, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    public static void init(Widget widget) {

    }
}
