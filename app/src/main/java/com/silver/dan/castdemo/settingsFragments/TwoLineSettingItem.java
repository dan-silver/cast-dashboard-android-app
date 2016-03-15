package com.silver.dan.castdemo.settingsFragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.silver.dan.castdemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TwoLineSettingItem extends SettingItem {
    @Bind(R.id.two_line_settings_item_header)
    TextView header;

    @Bind(R.id.two_line_settings_item_sub_header)
    TextView subHeader;

    public TwoLineSettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TwoLineSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.two_line_settings_item, this, true);
        ButterKnife.bind(this);
    }

    public void setHeaderText(String text) {
        header.setText(text);
    }

    public void setSubHeaderText(String text) {
        subHeader.setText(text);
    }

    public void setHeaderText(int resId) {
        setHeaderText(getResources().getString(resId));
    }
    public void setSubHeaderText(int resId) {
        setSubHeaderText(getResources().getString(resId));
    }

}
