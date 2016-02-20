package com.silvr.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.Widget;

import butterknife.ButterKnife;

public class PlaceholderSettings extends WidgetSettingsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.placeholder_settings, container, false);
        ButterKnife.bind(this, view);


        return view;
    }
}
