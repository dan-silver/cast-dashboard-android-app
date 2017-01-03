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

import java.util.HashMap;

public class AppSettingsBindings extends BaseObservable {

    @Bindable
    public Integer dashBackgroundColor;

    @Bindable
    public Integer numberOfColumns;

    @Bindable
    public BackgroundType backgroundType;

    @Bindable
    public Integer widgetTransparency;

    @Bindable
    public Integer widgetColor;

    @Bindable
    public Integer textColor;

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


    static String SHARED_PREFS_OPTIONS = "SHARED_PREFS_OPTIONS";

    public interface onLoadCallback {
        void onReady();

        void onError();
    }


    public AppSettingsBindings() {
        addOnPropertyChangedCallback(new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                saveAllSettings();
            }
        });
    }

    public void loadSettings(Context context, final onLoadCallback callback) {
        loadAllSettingsFromFirebase(context, callback);
    }

    @Exclude
    public void init(AppSettingsHelperFragment appSettings) {
        this.appSettings = appSettings;

    }

    @Exclude
    public void setDashBackgroundColor(int dashBackgroundColor) {
        this.dashBackgroundColor = dashBackgroundColor;
        notifyPropertyChanged(BR.dashBackgroundColor);
        appSettings.mCallback.onSettingChanged(BACKGROUND_COLOR, getBackgroundColorHexStr());
    }

    @Exclude
    public void setWidgetColor(int widgetColor) {
        this.widgetColor = widgetColor;
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
        this.textColor = textColor;
        notifyPropertyChanged(BR.textColor);
        appSettings.mCallback.onSettingChanged(TEXT_COLOR, getTextColorHextStr());
    }

    @Exclude
    public String getTextColorHextStr() {
        return Integer.toHexString(textColor).substring(2);
    }

    @Exclude
    public String getBackgroundColorHexStr() {
        return Integer.toHexString(dashBackgroundColor).substring(2);
    }

    @Exclude
    public String getWidgetColorHexStr() {
        return Integer.toHexString(widgetColor).substring(2);
    }

    @Exclude
    public int getNumberOfColumnsUI() {
        return this.numberOfColumns + 1;
    }

    @Exclude
    public String getBackgroundTypeUI() {
        return this.backgroundType.name();
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
    public void setBackgroundType(BackgroundType type) {
        this.backgroundType = type;
        notifyPropertyChanged(BR.backgroundType);
        appSettings.mCallback.onSettingChanged(BACKGROUND_TYPE, getBackgroundTypeUI());
    }

    @Exclude
    protected static DatabaseReference getFirebaseDashboardOptionsRef() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        return mDatabase
                .child("users")
                .child(LoginActivity.user.getUid())
                .child("dashboards")
                .child(FirebaseMigration.dashboardId)
                .child("options");
    }


    @Exclude
    public void saveAllSettings() {
        HashMap<String, Object> settings = new HashMap<>();

        settings.put(COLUMN_COUNT, numberOfColumns);
        settings.put(BACKGROUND_COLOR, dashBackgroundColor);
        settings.put(BACKGROUND_TYPE, backgroundType.getValue());
        settings.put(WIDGET_TRANSPARENCY, widgetTransparency);
        settings.put(WIDGET_COLOR, widgetColor);
        settings.put(TEXT_COLOR, textColor);
        settings.put(SLIDESHOW_INTERVAL, slideshowInterval);
        settings.put(SCREEN_PADDING, screenPadding);

        getFirebaseDashboardOptionsRef().setValue(settings);

    }

    @Exclude
    public void loadAllSettingsFromFirebase(final Context context, final onLoadCallback callback) {

        getFirebaseDashboardOptionsRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AppSettingsBindings tempSettings = dataSnapshot.getValue(AppSettingsBindings.class);

                // Don't use setters here because we don't want to trigger a sendMessage() to TV
                if (tempSettings.numberOfColumns == null) {
                    numberOfColumns = 2;
                } else {
                    numberOfColumns = tempSettings.numberOfColumns;
                }

                if (tempSettings.dashBackgroundColor == null) {
                    dashBackgroundColor = ContextCompat.getColor(context, R.color.tv_background);
                } else {
                    dashBackgroundColor = tempSettings.dashBackgroundColor;
                }

                if (tempSettings.widgetColor == null) {
                    widgetColor = ContextCompat.getColor(context, R.color.md_material_blue_800);
                } else {
                    widgetColor = tempSettings.widgetColor;
                }

                if (tempSettings.textColor == null) {
                    textColor = ContextCompat.getColor(context, R.color.tv_text_light);
                } else {
                    textColor = tempSettings.textColor;
                }

                if (tempSettings.backgroundType == null) {
                    backgroundType = BackgroundType.values()[0];
                } else {
                    backgroundType = tempSettings.backgroundType;
                }

                if (tempSettings.widgetTransparency == null) {
                    widgetTransparency = 15; //15% x 2 + 50 = 80/100
                } else {
                    widgetTransparency = tempSettings.widgetTransparency;
                }

                if (tempSettings.screenPadding == null) {
                    screenPadding = 15;
                } else {
                    screenPadding = tempSettings.screenPadding;
                }

                if (tempSettings.slideshowInterval == null) {
                    slideshowInterval = 30;
                } else {
                    slideshowInterval = tempSettings.slideshowInterval;
                }
                callback.onReady();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError();
            }
        });

    }

    @Exclude
    public void loadAllSettingsFromSharedPreferences(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS_OPTIONS, 0);

        // Don't use setters here because we don't want to trigger a sendMessage() to TV
        numberOfColumns = settings.getInt(COLUMN_COUNT, 2);
        dashBackgroundColor = settings.getInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.tv_background));
        widgetColor = settings.getInt(WIDGET_COLOR, ContextCompat.getColor(context, R.color.md_material_blue_800));
        textColor = settings.getInt(TEXT_COLOR, ContextCompat.getColor(context, R.color.tv_text_light));

        backgroundType = BackgroundType.values()[settings.getInt(BACKGROUND_TYPE, 0)];
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
        return this.backgroundType;
    }

    @Exclude
    public int getSlideshowInterval() {
        return slideshowInterval;
    }
}