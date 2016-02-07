package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larswerkman.lobsterpicker.LobsterPicker;
import com.larswerkman.lobsterpicker.OnColorListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppSettings extends Fragment {

    OnSettingChanged mCallback;

    static String COLUMN_COUNT = "COLUMN_COUNT";

    @Bind(R.id.seekBar)
    SeekBar columnCount;

    @Bind(R.id.num_columns_label)
    TextView numColumnsLabel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        ButterKnife.bind(this, view);

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
        return view;
    }

    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();

        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_OPTIONS_ITEM);
        super.onResume();
    }
}
