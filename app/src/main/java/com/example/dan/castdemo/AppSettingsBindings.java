package com.example.dan.castdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v4.content.ContextCompat;

import com.example.dan.castdemo.Settings.BackgroundType;

public class AppSettingsBindings extends BaseObservable {
    @Bindable
    public Integer widgetBackgroundColor;

    @Bindable
    public Integer numberOfColumns;

    @Bindable
    public BackgroundType backgroundType;

    @Bindable
    public int widgetTransparency;

    @Bindable
    public int widgetColor;

    private AppSettings appSettings;

    static String COLUMN_COUNT = "COLUMN_COUNT";
    static String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    static String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    static String WIDGET_TRANSPARENCY = "WIDGET_TRANSPARENCY";
    static String WIDGET_COLOR = "WIDGET_COLOR";

    static String SHARED_PREFS_OPTIONS = "SHARED_PREFS_OPTIONS";

    public AppSettingsBindings() {
        addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                saveAllSettings();
            }
        });
    }

    public void init(AppSettings appSettings) {
        this.appSettings = appSettings;

        loadAllSettings(appSettings.getContext());
    }

    public void setWidgetBackgroundColor(int widgetBackgroundColor) {
        this.widgetBackgroundColor = widgetBackgroundColor;
        notifyPropertyChanged(BR.widgetBackgroundColor);
        appSettings.mCallback.onSettingChanged(BACKGROUND_COLOR, getBackgroundColorHexStr());
    }


    public void setWidgetColor(int widgetColor) {
        this.widgetColor = widgetColor;
        notifyPropertyChanged(BR.widgetColor);
        appSettings.mCallback.onSettingChanged(WIDGET_COLOR, getWidgetColorHexStr());
    }

    public String getBackgroundColorHexStr() {
        return Integer.toHexString(widgetBackgroundColor).substring(2);
    }

    public String getWidgetColorHexStr() {
        return Integer.toHexString(widgetColor).substring(2);
    }

    public int getNumberOfColumnsUI() {
        return this.numberOfColumns + 1;
    }


    public String getBackgroundTypeUI() {
        return this.backgroundType.name();
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        notifyPropertyChanged(BR.numberOfColumns);
        appSettings.mCallback.onSettingChanged(COLUMN_COUNT, getNumberOfColumnsUI());
    }

    public void setBackgroundType(BackgroundType type) {
        this.backgroundType = type;
        notifyPropertyChanged(BR.backgroundType);
        appSettings.mCallback.onSettingChanged(BACKGROUND_TYPE, getBackgroundTypeUI());
    }

    public void saveAllSettings() {
        SharedPreferences preferences = appSettings.getContext().getSharedPreferences(SHARED_PREFS_OPTIONS, 0);
        SharedPreferences.Editor edit= preferences.edit();

        edit.putInt(COLUMN_COUNT, numberOfColumns);
        edit.putInt(BACKGROUND_COLOR, widgetBackgroundColor);
        edit.putInt(BACKGROUND_TYPE, backgroundType.getValue());
        edit.putInt(WIDGET_TRANSPARENCY, widgetTransparency);
        edit.putInt(WIDGET_COLOR, widgetColor);
        edit.apply();
    }

    public void loadAllSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_OPTIONS, 0);

        // Don't use setters here because we don't want to trigger a sendMessage() to TV
        numberOfColumns = settings.getInt(COLUMN_COUNT, 3);
        widgetBackgroundColor = settings.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.accent));
        widgetColor = settings.getInt(WIDGET_COLOR, ContextCompat.getColor(context, R.color.md_material_blue_800));

        backgroundType = BackgroundType.values()[settings.getInt(BACKGROUND_TYPE, 0)];
        widgetTransparency = settings.getInt(WIDGET_TRANSPARENCY, 80);

    }

    public int getWidgetTransparencyUI() {
        return widgetTransparency * 4;
    }

    public void setWidgetTransparency(int widgetTransparency) {
        this.widgetTransparency = widgetTransparency;
        notifyPropertyChanged(BR.widgetTransparency);
        appSettings.mCallback.onSettingChanged(WIDGET_TRANSPARENCY, getWidgetTransparencyUI());
    }
}