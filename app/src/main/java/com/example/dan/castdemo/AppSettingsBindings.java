package com.example.dan.castdemo;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingMethod;
import android.support.v4.content.ContextCompat;

public class AppSettingsBindings extends BaseObservable {
    public Integer widgetBackgroundColor;
    public Integer numberOfColumns;

    public AppSettingsBindings() {

    }

    public void init(AppSettings appSettings) {
        widgetBackgroundColor = ContextCompat.getColor(appSettings.getContext(), R.color.accent);
        numberOfColumns = 4;
    }

    public void setWidgetBackgroundColor(int widgetBackgroundColor) {
        this.widgetBackgroundColor = widgetBackgroundColor;
        notifyPropertyChanged(BR.widgetBackgroundColor);
    }

    @Bindable
    public int getWidgetBackgroundColor() {
        return this.widgetBackgroundColor;
    }

    @Bindable
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        notifyPropertyChanged(BR.numberOfColumns);
        notifyPropertyChanged(BR.numberOfColumnsStr);
    }

    @Bindable
    public String getNumberOfColumnsStr() {
        return "Number of columns: " + (numberOfColumns+1);
    }
}