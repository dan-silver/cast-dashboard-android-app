package com.silver.dan.castdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v4.content.ContextCompat;

import com.silver.dan.castdemo.SettingEnums.BackgroundType;

public class AppSettingsBindings extends BaseObservable {
    @Bindable
    public Integer dashBackgroundColor;

    @Bindable
    public Integer numberOfColumns;

    @Bindable
    public BackgroundType backgroundType;

    @Bindable
    public int widgetTransparency;

    @Bindable
    public int widgetColor;

    @Bindable
    public int textColor;

    @Bindable
    public int screenPadding;

    @Bindable
    public String backgroundImageLocalPath;


    public AppSettingsHelperFragment appSettings;

    static String COLUMN_COUNT = "COLUMN_COUNT";
    static String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    static String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    static String WIDGET_TRANSPARENCY = "WIDGET_TRANSPARENCY";
    static String WIDGET_COLOR = "WIDGET_COLOR";
    static String TEXT_COLOR = "TEXT_COLOR";
    static String SCREEN_PADDING = "SCREEN_PADDING";
    static String LOCALE = "LOCALE";
    static String BACKGROUND_IMAGE_LOCAL_PATH = "BACKGROUND_IMAGE_LOCAL_PATH";



    static String SHARED_PREFS_OPTIONS = "SHARED_PREFS_OPTIONS";

    public AppSettingsBindings() {
        addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                saveAllSettings();
            }
        });
    }

    public void init(AppSettingsHelperFragment appSettings) {
        this.appSettings = appSettings;

        loadAllSettings(appSettings.getContext());
    }

    public void setDashBackgroundColor(int dashBackgroundColor) {
        this.dashBackgroundColor = dashBackgroundColor;
        notifyPropertyChanged(BR.dashBackgroundColor);
        appSettings.mCallback.onSettingChanged(BACKGROUND_COLOR, getBackgroundColorHexStr());
    }

    public void setWidgetColor(int widgetColor) {
        this.widgetColor = widgetColor;
        notifyPropertyChanged(BR.widgetColor);
        appSettings.mCallback.onSettingChanged(WIDGET_COLOR, getWidgetColorHexStr());
    }



    public void setBackgroundImageLocalPath(String path) {
        this.backgroundImageLocalPath = path;
        notifyPropertyChanged(BR.backgroundImageLocalPath);
    }

    public String getBackgroundImageLocalPath() {
        return this.backgroundImageLocalPath;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        notifyPropertyChanged(BR.textColor);
        appSettings.mCallback.onSettingChanged(TEXT_COLOR, getTextColorHextStr());
    }

    public String getTextColorHextStr() {
        return Integer.toHexString(textColor).substring(2);
    }

    public String getBackgroundColorHexStr() {
        return Integer.toHexString(dashBackgroundColor).substring(2);
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


    public void setScreenPadding(int screenPadding) {
        this.screenPadding = screenPadding;
        notifyPropertyChanged(BR.screenPadding);
        appSettings.mCallback.onSettingChanged(SCREEN_PADDING, getScreenPaddingUI());
    }

    public void setBackgroundType(BackgroundType type) {
        this.backgroundType = type;
        notifyPropertyChanged(BR.backgroundType);
        appSettings.mCallback.onSettingChanged(BACKGROUND_TYPE, getBackgroundTypeUI());
    }

    public void saveAllSettings() {
        SharedPreferences preferences = appSettings.getContext().getSharedPreferences(SHARED_PREFS_OPTIONS, 0);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putInt(COLUMN_COUNT, numberOfColumns);
        edit.putInt(BACKGROUND_COLOR, dashBackgroundColor);
        edit.putInt(BACKGROUND_TYPE, backgroundType.getValue());
        edit.putInt(WIDGET_TRANSPARENCY, widgetTransparency);
        edit.putInt(WIDGET_COLOR, widgetColor);
        edit.putInt(TEXT_COLOR, textColor);
        edit.putInt(SCREEN_PADDING, screenPadding);
        edit.putString(BACKGROUND_IMAGE_LOCAL_PATH, backgroundImageLocalPath);
        edit.apply();
    }

    public void loadAllSettings(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_OPTIONS, 0);

        // Don't use setters here because we don't want to trigger a sendMessage() to TV
        numberOfColumns = settings.getInt(COLUMN_COUNT, 2);
        dashBackgroundColor = settings.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.tv_background));
        widgetColor = settings.getInt(WIDGET_COLOR, ContextCompat.getColor(context, R.color.md_material_blue_800));
        textColor = settings.getInt(TEXT_COLOR, ContextCompat.getColor(context, R.color.tv_text_light));

        backgroundType = BackgroundType.values()[settings.getInt(BACKGROUND_TYPE, 0)];
        widgetTransparency = settings.getInt(WIDGET_TRANSPARENCY, 15); //15% x 2 + 50 = 80/100
        screenPadding = settings.getInt(SCREEN_PADDING, 15);

        backgroundImageLocalPath = settings.getString(BACKGROUND_IMAGE_LOCAL_PATH, "");

    }

    public int getWidgetTransparencyUI() { // must return %/100
        return 2 * widgetTransparency + 50;
    }

    public void setWidgetTransparency(int widgetTransparency) {
        this.widgetTransparency = widgetTransparency;
        notifyPropertyChanged(BR.widgetTransparency);
        appSettings.mCallback.onSettingChanged(WIDGET_TRANSPARENCY, getWidgetTransparencyUI());
    }

    public int getScreenPaddingUI() {
        return screenPadding;
    }

    public BackgroundType getBackgroundType() {
        return this.backgroundType;
    }
}