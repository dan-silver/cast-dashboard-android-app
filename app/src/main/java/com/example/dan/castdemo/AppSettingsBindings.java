package com.example.dan.castdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v4.content.ContextCompat;

import com.example.dan.castdemo.Settings.BackgroundType;

public class AppSettingsBindings extends BaseObservable {
    public Integer widgetBackgroundColor;
    public Integer numberOfColumns;
    public BackgroundType backgroundType;

    private AppSettings appSettings;

    static String COLUMN_COUNT = "COLUMN_COUNT";
    static String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    static String BACKGROUND_COLOR = "BACKGROUND_COLOR";

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

    public String getBackgroundColorHexStr() {
        return Integer.toHexString(widgetBackgroundColor).substring(2);
    }

    @Bindable
    public int getWidgetBackgroundColor() {
        return this.widgetBackgroundColor;
    }

    @Bindable
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public int getNumberOfColumnsUI() {
        return this.numberOfColumns + 1;
    }


    @Bindable
    public BackgroundType getBackgroundType() {
        return this.backgroundType;
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

        edit.putInt(COLUMN_COUNT, getNumberOfColumns());
        edit.putInt(BACKGROUND_COLOR, getWidgetBackgroundColor());
        edit.putInt(BACKGROUND_TYPE, getBackgroundType().getValue());
        edit.apply();
    }

    public void loadAllSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_OPTIONS, 0);

        // Don't use setters here because we don't want to trigger a sendMessage() to TV
        numberOfColumns = settings.getInt(COLUMN_COUNT, 3);
        widgetBackgroundColor = settings.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.accent));

        backgroundType = BackgroundType.values()[settings.getInt(BACKGROUND_TYPE, 0)];
    }
}