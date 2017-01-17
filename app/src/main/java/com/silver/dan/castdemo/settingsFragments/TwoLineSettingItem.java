package com.silver.dan.castdemo.settingsFragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.silver.dan.castdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TwoLineSettingItem extends SettingItem {
    @BindView(R.id.two_line_settings_item_header)
    TextView header;

    @BindView(R.id.two_line_settings_item_sub_header)
    TextView subHeader;

    @BindView(R.id.two_line_settings_item_icon)
    ImageView icon;

    public TwoLineSettingItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TwoLineSettingItem);


        header.setText(a.getString(R.styleable.TwoLineSettingItem_headerText));
        subHeader.setText(a.getString(R.styleable.TwoLineSettingItem_subHeaderText));

        a.recycle();
    }

    public TwoLineSettingItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.two_line_settings_item, this, true);
        ButterKnife.bind(this);
        init(attrs);
    }

    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            icon.setVisibility(View.GONE);
            return;
        }

        icon.setImageDrawable(drawable);
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
