package com.silver.dan.castdemo.settingsFragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.silver.dan.castdemo.R;


public class SettingItem extends FrameLayout {
    private FrameLayout mContentView;

    public SettingItem(Context context) {
        this(context, null);
    }


    public SettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Inflate and attach your child XML
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.setting_item_layout, this, true);

        //Get a reference to the layout where you want children to be placed
        mContentView = (FrameLayout) findViewById(R.id.content);

        //Do any more custom init you would like to access children and do setup

    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if(mContentView == null){
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            mContentView.addView(child, index, params);
        }
    }
}
