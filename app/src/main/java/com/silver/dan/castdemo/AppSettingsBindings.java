package com.silver.dan.castdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silver.dan.castdemo.SettingEnums.BackgroundType;
import com.silver.dan.castdemo.Util.ColorConverter;

import java.util.HashMap;

public class AppSettingsBindings extends BaseObservable {

    @Bindable
    public String dashBackgroundColor;

    @Bindable
    public Integer numberOfColumns;

    @Bindable
    public Integer backgroundType;

    @Bindable
    public Integer widgetTransparency;

    @Bindable
    public String widgetColor;

    @Bindable
    public String textColor;

    @Bindable
    public Integer screenPadding;

    @Bindable
    public Integer slideshowInterval;


    @Exclude
    public AppSettingsHelperFragment appSettings;

    static String COLUMN_COUNT = "COLUMN_COUNT";
    static String BACKGROUND_TYPE = "BACKGROUND_TYPE";
    static String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    static String WIDGET_TRANSPARENCY = "WIDGET_TRANSPARENCY";
    static String WIDGET_COLOR = "WIDGET_COLOR";
    static String TEXT_COLOR = "TEXT_COLOR";
    static String SCREEN_PADDING = "SCREEN_PADDING";
    static String LOCALE = "LOCALE";
    static String SLIDESHOW_INTERVAL = "SLIDESHOW_INTERVAL";


    @Deprecated
    static String SHARED_PREFS_OPTIONS = "SHARED_PREFS_OPTIONS";


    public AppSettingsBindings() {
    }

    public void initDefaults(Context context) {
        // default settings
        if (dashBackgroundColor == null)
            dashBackgroundColor = ColorConverter.intToString(ContextCompat.getColor(context, R.color.tv_background));

        if (numberOfColumns == null)
            numberOfColumns = 2;

        if (backgroundType == null)
            backgroundType = BackgroundType.SLIDESHOW.getValue();

        if (widgetTransparency == null)
            widgetTransparency = 15; //15% x 2 + 50 = 80/100

        if (widgetColor == null)
            widgetColor = ColorConverter.intToString(ContextCompat.getColor(context, R.color.md_material_blue_800));

        if (textColor == null)
            textColor = ColorConverter.intToString(ContextCompat.getColor(context, R.color.tv_text_light));

        if (screenPadding == null)
            screenPadding = 15;

        if (slideshowInterval == null)
            slideshowInterval = 30;

        addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                saveAllSettings();
            }
        });
    }

    @Exclude
    public void init(AppSettingsHelperFragment appSettings) {
        this.appSettings = appSettings;

    }

    @Exclude
    public void setDashBackgroundColor(int dashBackgroundColor) {
        this.dashBackgroundColor = ColorConverter.intToString(dashBackgroundColor);
        notifyPropertyChanged(BR.dashBackgroundColor);
        appSettings.mCallback.onSettingChanged(BACKGROUND_COLOR, getBackgroundColorHexStr());
    }

    @Exclude
    public void setWidgetColor(int widgetColor) {
        this.widgetColor = ColorConverter.intToString(widgetColor);
        notifyPropertyChanged(BR.widgetColor);
        appSettings.mCallback.onSettingChanged(WIDGET_COLOR, getWidgetColorHexStr());
    }

    @Exclude
    public void setSlideshowInterval(int interval) {
        this.slideshowInterval = interval;
        notifyPropertyChanged(BR.slideshowInterval);
        appSettings.mCallback.onSettingChanged(SLIDESHOW_INTERVAL, getSlideshowInterval());
    }

    @Exclude
    public void setTextColor(int textColor) {
        this.textColor = ColorConverter.intToString(textColor);
        notifyPropertyChanged(BR.textColor);
        appSettings.mCallback.onSettingChanged(TEXT_COLOR, getTextColorHextStr());
    }

    @Exclude
    public void setTextColor(String textColor) {
        this.textColor = textColor;
        notifyPropertyChanged(BR.textColor);
        appSettings.mCallback.onSettingChanged(TEXT_COLOR, getTextColorHextStr());
    }

    @Exclude
    public String getTextColorHextStr() {
        return textColor;
    }

    @Exclude
    public String getBackgroundColorHexStr() {
        return dashBackgroundColor;
    }

    @Exclude
    public String getWidgetColorHexStr() {
        return widgetColor;
    }

    @Exclude
    public int getNumberOfColumnsUI() {
        return this.numberOfColumns + 1;
    }

    @Exclude
    public String getBackgroundTypeUI() {
        return BackgroundType.values()[this.backgroundType].name();
    }

    @Exclude
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        notifyPropertyChanged(BR.numberOfColumns);
        appSettings.mCallback.onSettingChanged(COLUMN_COUNT, getNumberOfColumnsUI());
    }

    @Exclude
    public void setScreenPadding(int screenPadding) {
        this.screenPadding = screenPadding;
        notifyPropertyChanged(BR.screenPadding);
        appSettings.mCallback.onSettingChanged(SCREEN_PADDING, getScreenPaddingUI());
    }

    @Exclude
    public void setBackgroundType(Integer type) {
        this.backgroundType = type;
        notifyPropertyChanged(BR.backgroundType);
        appSettings.mCallback.onSettingChanged(BACKGROUND_TYPE, getBackgroundTypeUI());
    }

    @Exclude
    protected static DatabaseReference getFirebaseDashboardOptionsRef() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase
            .child("users")
            .child(AuthHelper.user.getUid())
            .child("options");
    }


    @Exclude
    public void saveAllSettings() {
        HashMap<String, Object> settings = new HashMap<>();

        settings.put("numberOfColumns", numberOfColumns);
        settings.put("dashBackgroundColor", dashBackgroundColor);
        settings.put("backgroundType", backgroundType);
        settings.put("widgetTransparency", widgetTransparency);
        settings.put("widgetColor", widgetColor);
        settings.put("textColor", textColor);
        settings.put("slideshowInterval", slideshowInterval);
        settings.put("screenPadding", screenPadding);

        getFirebaseDashboardOptionsRef().setValue(settings);

    }

    @Exclude
    public void loadAllSettingsFromSharedPreferences(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_OPTIONS, 0);

        // Don't use setters here because we don't want to trigger a sendMessage() to TV
        numberOfColumns = settings.getInt(COLUMN_COUNT, 2);
        int iDashBackgroundColor = settings.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.tv_background));
        dashBackgroundColor = ColorConverter.intToString(iDashBackgroundColor);

        int iWidgetColor = settings.getInt(WIDGET_COLOR, ContextCompat.getColor(context, R.color.md_material_blue_800));
        widgetColor = ColorConverter.intToString(iWidgetColor);

        int iTextColor = settings.getInt(TEXT_COLOR, ContextCompat.getColor(context, R.color.tv_text_light));
        textColor = ColorConverter.intToString(iTextColor);

        backgroundType = settings.getInt(BACKGROUND_TYPE, 0);
        if (BackgroundType.values()[backgroundType] == BackgroundType.PICTURE) {
            backgroundType = BackgroundType.SLIDESHOW.getValue();
        }


        widgetTransparency = settings.getInt(WIDGET_TRANSPARENCY, 15); //15% x 2 + 50 = 80/100
        screenPadding = settings.getInt(SCREEN_PADDING, 15);

        slideshowInterval = settings.getInt(SLIDESHOW_INTERVAL, 30);
    }

    @Exclude
    public int getWidgetTransparencyUI() { // must return %/100
        return 2 * widgetTransparency + 50;
    }

    @Exclude
    public void setWidgetTransparency(int widgetTransparency) {
        this.widgetTransparency = widgetTransparency;
        notifyPropertyChanged(BR.widgetTransparency);
        appSettings.mCallback.onSettingChanged(WIDGET_TRANSPARENCY, getWidgetTransparencyUI());
    }

    @Exclude
    public int getScreenPaddingUI() {
        return screenPadding;
    }

    @Exclude
    public BackgroundType getBackgroundType() {
        return BackgroundType.values()[this.backgroundType];
    }

    @Exclude
    public int getSlideshowInterval() {
        return slideshowInterval;
    }
}