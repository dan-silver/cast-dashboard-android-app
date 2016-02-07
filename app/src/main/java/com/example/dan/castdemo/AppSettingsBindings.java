package com.example.dan.castdemo;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.content.ContextCompat;

public class AppSettingsBindings extends BaseObservable {
    public Integer widgetBackgroundColor;

    public AppSettingsBindings() {

    }

    public void init(AppSettings appSettings) {
        widgetBackgroundColor = ContextCompat.getColor(appSettings.getContext(), R.color.accent);
    }

    public void setWidgetBackgroundColor(int widgetBackgroundColor) {
        this.widgetBackgroundColor = widgetBackgroundColor;
        notifyPropertyChanged(BR.widgetBackgroundColor);
    }

    @Bindable
    public int getWidgetBackgroundColor() {
        return this.widgetBackgroundColor;
    }
}