package com.example.dan.castdemo.settingsFragments;

import android.support.v4.app.Fragment;

import com.example.dan.castdemo.CastCommunicator;
import com.example.dan.castdemo.Widget;

public class WidgetSettingsFragment extends Fragment {
    protected Widget widget;

    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget);
    }
}


