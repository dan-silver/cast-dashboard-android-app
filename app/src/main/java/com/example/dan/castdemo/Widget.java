package com.example.dan.castdemo;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public abstract class Widget {

    public static String ID = "WIDGET_ID";
    public static String TYPE = "WIDGET_TYPE";


    public static int CALENDAR_TYPE = 1;
    public static int PLACEHOLDER_TYPE = 2;

    public int id;
    public int type;

    public Context context;
    abstract public String getHumanName();

    public Widget(Context context) {
        this.context = context;
        //@todo
        Random rand = new Random();

        this.id = rand.nextInt();
    }

}
