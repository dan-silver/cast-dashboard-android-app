package com.example.dan.castdemo;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.example.dan.castdemo.Settings.BackgroundType;
import com.example.dan.castdemo.databinding.FragmentAppSettingsBinding;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppSettings extends Fragment {

    OnSettingChanged mCallback;
    FragmentAppSettingsBinding viewModel;

    @Bind(R.id.seekBar)
    SeekBar columnCount;

    @Bind(R.id.background_type_spinner)
    Spinner backgroundTypeSpinner;

    @Bind(R.id.widget_transparency)
    SeekBar widgetTransparency;

    private AppSettingsBindings bindings;

    public AppSettings() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        ButterKnife.bind(this, view);



        viewModel = FragmentAppSettingsBinding.bind(view);
        bindings = new AppSettingsBindings();
        bindings.init(this);
        viewModel.setSettings(bindings);

        columnCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    bindings.setNumberOfColumns(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        widgetTransparency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    bindings.setWidgetTransparency(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final ArrayAdapter<BackgroundType> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, BackgroundType.values());

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        backgroundTypeSpinner.setAdapter(adapter);
        backgroundTypeSpinner.setSelection(bindings.backgroundType.getValue());

        backgroundTypeSpinner.post(new Runnable() {
            public void run() {
                backgroundTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        bindings.setBackgroundType(adapter.getItem(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });


            return view;
        }

        @OnClick(R.id.widget_background_color)
    public void openWidgetBackgroundColorDialog() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(bindings.widgetBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(5)
                .setPositiveButton("Done", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                        bindings.setWidgetBackgroundColor(selectedColor);
                    }
                })
                .build()
                .show();
    }


    @OnClick(R.id.widget_color)
    public void openWidgetColorDialog() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("Choose color")
                .initialColor(bindings.widgetColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(5)
                .setPositiveButton("Done", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                        bindings.setWidgetColor(selectedColor);
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();

        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_OPTIONS_ITEM);
        super.onResume();
    }
}
