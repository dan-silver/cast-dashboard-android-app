package com.silver.dan.castdemo.Util;

import android.graphics.Color;

/**
 * Created by dan on 1/2/17.
 */

public class ColorConverter {
    public static String intToString(int color) {
        return Integer.toHexString(color).substring(2);
    }

    public static Integer stringToInt(String color) {
        return Color.parseColor("#" + color);
    }
}