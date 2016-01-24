package com.example.dan.castdemo;

import com.example.dan.castdemo.widgets.CalendarWidget;
import com.example.dan.castdemo.widgets.PlaceholderWidget;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


@Table(database = WidgetDatabase.class)
public class Widget extends BaseModel {

    // For bundle data passing
    public static String ID = "WIDGET_ID";

    enum types {
        CALENDAR,
        PLACEHOLDER
    }

    @PrimaryKey(autoincrement = true)
    public long id;

    @Column
    public int type;

    public int getIconResource() {
        int typeIconResource;
        Widget.types type = getWidgetType();

        switch (type) {
            case CALENDAR:
                typeIconResource = R.drawable.ic_today_24dp;
                break;
            default:
                typeIconResource = R.drawable.ic_hourglass_empty_black_24px;
        }
        return typeIconResource;
    }

    final static String[] widgetNames = new String[]{
            CalendarWidget.HUMAN_NAME,
            PlaceholderWidget.HUMAN_NAME
    };

    public String getHumanName() {
        return widgetNames[type];
    }

    public Widget() {
    }

    public void setType(int type) {
        this.type = type;
    }


    //@todo clean this up!
    public types getWidgetType() {
        if (type == 0) {
            return types.CALENDAR;
        } else {//if (type == 1) {
            return types.PLACEHOLDER;
        }
    }
}
