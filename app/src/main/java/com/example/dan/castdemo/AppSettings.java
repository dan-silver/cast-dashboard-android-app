package com.example.dan.castdemo;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dan.castdemo.databinding.FragmentAppSettingsBinding;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppSettings extends Fragment {

    OnSettingChanged mCallback;
    AppSettingsBindings viewModel;

    static String COLUMN_COUNT = "COLUMN_COUNT";

    @Bind(R.id.seekBar)
    SeekBar columnCount;

    @Bind(R.id.num_columns_label)
    TextView numColumnsLabel;

    @Bind(R.id.widget_background_color)
    FrameLayout widget_background_color;

    @Bind(R.id.background_type_spinner)
    Spinner backgroundTypeSpinner;

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

        viewModel = new AppSettingsBindings();
        viewModel.init(this);

        FragmentAppSettingsBinding binding = DataBindingUtil.bind(view);
        binding.setSettings(viewModel);

        columnCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress++; //UI is indexed off 1 (no zero column count allowed)
                mCallback.onSettingChanged(COLUMN_COUNT, String.valueOf(progress));
                numColumnsLabel.setText("Number of Columns: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.background_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        backgroundTypeSpinner.setAdapter(adapter);

        backgroundTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        widget_background_color.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ColorPickerDialogBuilder
                        .with(getContext())
                        .setTitle("Choose color")
                        .initialColor(viewModel.getWidgetBackgroundColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(5)
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                viewModel.setWidgetBackgroundColor(selectedColor);
                            }
                        })
                        .build()
                        .show();
                return false;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();

        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_OPTIONS_ITEM);
        super.onResume();
    }
}