package com.silver.dan.castdemo.Settings;

public enum BackgroundType {
    SLIDESHOW(0, "Slideshow"),
    SOLID_COLOR(1, "Solid Color");

    private int value;
    private String humanName;

    BackgroundType(int value, String humanName) {
        this.value = value;
        this.humanName = humanName;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.humanName;
    }
}
