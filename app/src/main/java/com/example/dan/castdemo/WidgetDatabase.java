package com.example.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = WidgetDatabase.NAME, version = WidgetDatabase.VERSION)

public class WidgetDatabase {

    public static final String NAME = "Widgets1";

    public static final int VERSION = 5;
}
