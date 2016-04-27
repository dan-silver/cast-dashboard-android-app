package com.silver.dan.castdemo.SettingEnums;

import com.silver.dan.castdemo.R;

public enum MapMode {
    STANDARD(0, R.string.standard),
    DIRRECTIONS(3, R.string.directions);

    private int value;
    private int humanNameRes;

    MapMode(int value, int humanNameRes) {
        this.value = value;
        this.humanNameRes = humanNameRes;
    }

    public int getValue() {
        return value;
    }

    public int getHumanNameRes() {
        return humanNameRes;
    }


    public static MapMode getMapMode(int value) {
        for (MapMode l : MapMode.values()) {
            if (l.value == value) return l;
        }
        throw new IllegalArgumentException("Map mode not found.");
    }
}