package com.silvr.dan.castdemo;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

}