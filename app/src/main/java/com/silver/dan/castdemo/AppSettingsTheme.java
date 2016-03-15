package com.silver.dan.castdemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.silver.dan.castdemo.Settings.BackgroundType;
import com.silver.dan.castdemo.databinding.FragmentAppSettingsThemeBinding;
import com.silver.dan.castdemo.settingsFragments.TwoLineSettingItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppSettingsTheme extends AppSettingsHelperFragment {

    @Bind(R.id.background_type)
    TwoLineSettingItem backgroundType;

    @Bind(R.id.widget_transparency)
    SeekBar widgetTransparency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings_theme, container, false);
        ButterKnife.bind(this, view);

        viewModel = FragmentAppSettingsThemeBinding.bind(view);
        bindings = new AppSettingsBindings();
        bindings.init(this);
        ((FragmentAppSettingsThemeBinding) viewModel).setSettings(bindings);
        backgroundType.setHeaderText(R.string.background);
        updateBackgroundTypeText();

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

        return view;
    }

    public void updateBackgroundTypeText() {
        backgroundType.setSubHeaderText(bindings.getBackgroundTypeStrRes());
    }

    @OnClick(R.id.background_type)
    public void setBackgroundType() {
        // it should be safe to rearrange and add items to this list
        final ArrayList<BackgroundType> backgroundTypes = new ArrayList<BackgroundType>(){{
            add(BackgroundType.SLIDESHOW);
            add(BackgroundType.SOLID_COLOR);
        }};

        new MaterialDialog.Builder(getContext())
                .title(R.string.background)
                .items(R.array.backgroundTypeList)
                .itemsCallbackSingleChoice(backgroundTypes.indexOf(bindings.getBackgroundType()), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        bindings.setBackgroundType(backgroundTypes.get(which));
                        updateBackgroundTypeText();
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.widget_background_color)
    public void openWidgetBackgroundColorDialog() {
        createColorPickerDialog(bindings.widgetBackgroundColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setWidgetBackgroundColor(selectedColor);
            }
        });
    }


    @OnClick(R.id.text_color_setting_item)
    public void openTextColorDialog() {
        createColorPickerDialog(bindings.textColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setTextColor(selectedColor);
            }
        });
    }

    @OnClick(R.id.widget_color_setting_item)
    public void openWidgetColorDialog() {
        createColorPickerDialog(bindings.widgetColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setWidgetColor(selectedColor);
            }
        });
    }


    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();

        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_OPTIONS_THEME_ITEM);
        super.onResume();
    }
}
