package com.silver.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = WidgetDatabase.NAME, version = WidgetDatabase.VERSION)

public class WidgetDatabase {

    public static final String NAME = "Widgets2";

    static final int VERSION = 1;
}
