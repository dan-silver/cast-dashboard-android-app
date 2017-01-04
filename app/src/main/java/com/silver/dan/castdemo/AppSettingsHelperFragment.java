package com.silver.dan.castdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.silver.dan.castdemo.Util.ColorConverter;

public class AppSettingsHelperFragment extends Fragment {

    OnSettingChangedListener mCallback;
    android.databinding.ViewDataBinding viewModel;
    AppSettingsBindings bindings;

    public AppSettingsHelperFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mCallback = (OnSettingChangedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnSettingChangedListener");
        }
    }

    public void createColorPickerDialog(String initialColor, ColorPickerClickListener onResult) {
        createColorPickerDialog(ColorConverter.stringToInt(initialColor), onResult);
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
