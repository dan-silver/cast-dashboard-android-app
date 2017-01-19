package com.silver.dan.castdemo.SettingEnums;

import com.silver.dan.castdemo.R;

public enum BackgroundType {
    SLIDESHOW(0, R.string.slideshow),
    SOLID_COLOR(1, R.string.solid_color),
    @Deprecated
    PICTURE(2, R.string.picture),
    PICASA_ALBUM(3, R.string.google_photos_album);

    private int value;
    private int humanNameRes;

    BackgroundType(int value, int humanNameRes) {
        this.value = value;
        this.humanNameRes = humanNameRes;
    }

    public int getValue() {
        return value;
    }

    public int getHumanNameRes() {
        return humanNameRes;
    }
}