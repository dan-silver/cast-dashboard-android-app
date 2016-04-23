package com.silver.dan.castdemo.SettingEnums;

import com.silver.dan.castdemo.R;

public enum WeatherType {
    TODAY(0, R.string.todays_summary),
    FIVE_DAY(1, R.string.five_day);

    private int value;
    private int humanNameRes;

    WeatherType(int value, int humanNameRes) {
        this.value = value;
        this.humanNameRes = humanNameRes;
    }

    public int getValue() {
        return value;
    }

    public int getHumanNameRes() {
        return humanNameRes;
    }


    public static WeatherType getEnumFromInt(int value) {
        for (WeatherType l : WeatherType.values()) {
            if (l.value == value) return l;
        }
        throw new IllegalArgumentException("Enum value not found.");
    }
}