package com.example.dan.castdemo.widgets;

import android.content.Context;

import com.example.dan.castdemo.Widget;

public class PlaceholderWidget extends Widget {
    @Override
    public String getHumanName() {
        return "Placeholder";
    }

    public PlaceholderWidget(Context context) {
        super(context);
        this.type = Widget.PLACEHOLDER_TYPE;
    }
}
