package com.silver.dan.castdemo;

interface OnSettingChangedListener {
    void onSettingChanged(String setting, String value);

    void onSettingChanged(String setting, int value);
}
