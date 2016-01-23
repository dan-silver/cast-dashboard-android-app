package com.example.dan.castdemo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Widget {

    public Widget() {}

    //helper method - given a widget class, instantiate it.
    //CalendarWidget.class -> new CalendarWidget()
    public static Widget createFromClass(Class widgetType) {

        try {
            Constructor<?> cons = (widgetType).getConstructor();
            return (Widget) cons.newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
