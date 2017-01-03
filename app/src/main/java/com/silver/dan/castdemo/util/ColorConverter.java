package com.silver.dan.castdemo.util;

/**
 * Created by dan on 1/2/17.
 */

public class ColorConverter {
    public static String intToString(int color) {
        return Integer.toHexString(color);
    }

    public static Integer stringToInteger(String color) {
        return (int) (long) Long.parseLong(color, 16);
    }
}