package com.example.dan.castdemo;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
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

    public AppSettingsBindings() {

    }

    public void init(AppSettings appSettings) {
        this.appSettings = appSettings;

        widgetBackgroundColor = ContextCompat.getColor(appSettings.getContext(), R.color.accent);
        numberOfColumns = 4;
        backgroundType = BackgroundType.SLIDESHOW;
    }

    public void setWidgetBackgroundColor(int widgetBackgroundColor) {
        this.widgetBackgroundColor = widgetBackgroundColor;
        notifyPropertyChanged(BR.widgetBackgroundColor);
        appSettings.mCallback.onSettingChanged(BACKGROUND_COLOR, Integer.toHexString(widgetBackgroundColor).substring(2));
    }


    @Bindable
    public int getWidgetBackgroundColor() {
        return this.widgetBackgroundColor;
    }

    @Bindable
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }


    @Bindable
    public BackgroundType getBackgroundType() {
        return this.backgroundType;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        notifyPropertyChanged(BR.numberOfColumns);
        appSettings.mCallback.onSettingChanged(COLUMN_COUNT, numberOfColumns + 1);
    }

    public void setBackgroundType(BackgroundType type) {
        this.backgroundType = type;
        notifyPropertyChanged(BR.backgroundType);
        appSettings.mCallback.onSettingChanged(BACKGROUND_TYPE, type.name());
    }
}