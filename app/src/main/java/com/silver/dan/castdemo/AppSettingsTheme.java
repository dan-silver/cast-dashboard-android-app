package com.silver.dan.castdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.silver.dan.castdemo.SettingEnums.BackgroundType;
import com.silver.dan.castdemo.databinding.FragmentAppSettingsThemeBinding;
import com.silver.dan.castdemo.settingsFragments.TwoLineSettingItem;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppSettingsTheme extends AppSettingsHelperFragment {

    private static final int SELECT_PHOTO = 0;
    private static final int MB = 1000000;

    @BindView(R.id.background_type)
    TwoLineSettingItem backgroundType;

    @BindView(R.id.widget_transparency)
    SeekBar widgetTransparency;

    @BindView(R.id.dashboard_background_picture)
    ImageView backgroundPicture;

    @BindView(R.id.upload_progress)
    ProgressBar uploadProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings_theme, container, false);
        ButterKnife.bind(this, view);

        viewModel = FragmentAppSettingsThemeBinding.bind(view);
        bindings = MainActivity.settings;
        bindings.init(this);
        ((FragmentAppSettingsThemeBinding) viewModel).setSettings(bindings);

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

    @OnClick(R.id.slideshowInterval)
    public void setSlideShowInterval() {
        final ArrayList<Integer> options = new ArrayList<>(Arrays.asList(10, 20, 30, 40, 50, 60));
        ArrayList<String> optionLabels = new ArrayList<>();
        for (Integer option : options) {
            optionLabels.add(option + " " + getString(R.string.seconds));
        }
        new MaterialDialog.Builder(getContext())
                .title(R.string.slideshowSpeed)
                .items(optionLabels)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        bindings.setSlideshowInterval(options.get(which));
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.background_type)
    public void setBackgroundType() {
        // it should be safe to rearrange and add items to this list
        final ArrayList<BackgroundType> backgroundTypes = new ArrayList<BackgroundType>() {{
            add(BackgroundType.SLIDESHOW);
            add(BackgroundType.SOLID_COLOR);
        }};

        new MaterialDialog.Builder(getContext())
                .title(R.string.background)
                .items(R.array.backgroundTypeList)
                .itemsCallbackSingleChoice(backgroundTypes.indexOf(bindings.getBackgroundType()), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        BackgroundType newBackgroundType = backgroundTypes.get(which);
                        bindings.setBackgroundType(newBackgroundType);
                        return true;
                    }


                })
                .show();
    }


    @OnClick(R.id.dashboard_background_picture)
    public void getBackgroundImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    @OnClick(R.id.widget_background_color)
    public void openWidgetBackgroundColorDialog() {
        createColorPickerDialog(bindings.dashBackgroundColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setDashBackgroundColor(selectedColor);
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
