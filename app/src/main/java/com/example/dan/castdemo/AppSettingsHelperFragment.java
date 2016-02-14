package com.example.dan.castdemo;

import android.databinding.tool.Binding;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class AppSettingsHelperFragment extends Fragment {

    OnSettingChanged mCallback;
    android.databinding.ViewDataBinding viewModel;
    AppSettingsBindings bindings;

    public AppSettingsHelperFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mCallback = (OnSettingChanged) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}
