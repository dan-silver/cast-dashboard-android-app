package com.silvr.dan.castdemo;

public interface OnSettingChanged {
    void onSettingChanged(String setting, String value);
    void onSettingChanged(String setting, int value);
}
