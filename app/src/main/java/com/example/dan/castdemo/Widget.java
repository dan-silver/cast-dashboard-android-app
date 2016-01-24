package com.example.dan.castdemo;

import android.content.Context;

import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.PlaceholderWidget;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Random;

@Table(database = WidgetDatabase.class)
public class Widget extends BaseModel {

    // For bundle data passing
    public static String ID = "WIDGET_ID";
    public static String TYPE = "WIDGET_TYPE";


    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int type;

    enum widgetTypes {
            CALENDAR,
            PLACEHOLDER
    }

    final static String[] widgetNames = new String[] {
            CalendarWidget.HUMAN_NAME,
            PlaceholderWidget.HUMAN_NAME
    };

    public String getHumanName() {
        return widgetNames[type];
    }

    public Widget() {}

    public void setType(int type) {
        this.type = type;
    }
}
