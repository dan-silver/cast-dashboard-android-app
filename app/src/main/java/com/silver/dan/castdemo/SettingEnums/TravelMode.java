package com.silver.dan.castdemo.SettingEnums;

import com.silver.dan.castdemo.R;

/**
 * Created by dan on 4/27/2016.
 */
public enum TravelMode {

    DRIVING(0, R.string.driving),
    BICYCLING(1, R.string.bicycling),
    TRANSIT(2, R.string.transit),
    WALKING(3, R.string.walking);

    private int value;
    private int humanNameRes;

    TravelMode(int value, int humanNameRes) {
        this.value = value;
        this.humanNameRes = humanNameRes;
    }

    public int getValue() {
        return value;
    }

    public int getHumanNameRes() {
        return humanNameRes;
    }


    public static TravelMode getMode(int value) {
        for (TravelMode l : TravelMode.values()) {
            if (l.value == value) return l;
        }
        throw new IllegalArgumentException("Mode not found.");
    }
}
