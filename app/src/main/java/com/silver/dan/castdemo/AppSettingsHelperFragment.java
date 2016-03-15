package com.silver.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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


    public void createColorPickerDialog(int initialColor, ColorPickerClickListener onResult) {
        ColorPickerDialogBuilder
                .with(getContext())
                .initialColor(initialColor)
                .showAlphaSlider(false)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(5)
                .setPositiveButton(getString(R.string.done), onResult)
                .build()
                .show();

    }

}
