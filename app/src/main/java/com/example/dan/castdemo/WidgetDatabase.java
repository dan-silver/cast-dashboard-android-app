package com.example.dan.castdemo;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Dan on 1/23/2016.
 */
@Database(name = WidgetDatabase.NAME, version = WidgetDatabase.VERSION)

public class WidgetDatabase {

    public static final String NAME = "Widgets";

    public static final int VERSION = 1;
}
