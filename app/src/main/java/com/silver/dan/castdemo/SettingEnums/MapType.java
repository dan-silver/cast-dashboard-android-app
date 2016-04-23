package com.silver.dan.castdemo.SettingEnums;

import com.silver.dan.castdemo.R;

public enum MapType {
    ROADMAP(0, R.string.roadmap),
    SATELLITE(1, R.string.satellite),
    HYBRID(2, R.string.hybrid),
    TERRAIN(3, R.string.terrain);

    private int value;
    private int humanNameRes;

    MapType(int value, int humanNameRes) {
        this.value = value;
        this.humanNameRes = humanNameRes;
    }

    public int getValue() {
        return value;
    }

    public int getHumanNameRes() {
        return humanNameRes;
    }


    public static MapType getMapType(int value) {
        for (MapType l : MapType.values()) {
            if (l.value == value) return l;
        }
        throw new IllegalArgumentException("Map type not found.");
    }
}