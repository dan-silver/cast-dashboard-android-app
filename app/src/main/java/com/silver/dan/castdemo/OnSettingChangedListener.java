package com.silver.dan.castdemo;

public interface OnSettingChangedListener {
    void onSettingChanged(String setting, String value);

    void onSettingChanged(String setting, int value);
}
