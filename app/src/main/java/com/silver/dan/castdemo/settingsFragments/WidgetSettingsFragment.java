package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.raizlabs.android.dbflow.sql.language.Select;
import com.silver.dan.castdemo.CastCommunicator;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.Widget_Table;

import butterknife.Bind;
import butterknife.OnClick;

public abstract class WidgetSettingsFragment extends Fragment {
    protected Widget widget;

    public static String WIDGET_HEIGHT = "WIDGET_HEIGHT";

    @Nullable
    @Bind(R.id.widget_height)
    TwoLineSettingItem widgetHeight;

    // percentage of screen height
    WidgetOption optionWidgetHeight;

    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget);
    }

    protected void updateWidgetProperty(String property, Object value) {
        CastCommunicator.sendWidgetProperty(widget, property, value);
    }

    protected WidgetOption loadOrInitOption(String property) {
        return widget.loadOrInitOption(property, getContext());
    }

    @Nullable
    @OnClick(R.id.widget_height)
    public void cycleWidgetHeight() {
        // options are 40, 60, 80, 100
        int currentHeight = optionWidgetHeight.getIntValue();
        if (currentHeight == 100) {
            optionWidgetHeight.update(40);
        } else {
            optionWidgetHeight.update(currentHeight + 20);
        }
        updateWidgetHeightText();
        updateWidgetProperty(WidgetSettingsFragment.WIDGET_HEIGHT, optionWidgetHeight.getIntValue());
    }

    public void updateWidgetHeightText() {
        if (widgetHeight != null)
            widgetHeight.setSubHeaderText(optionWidgetHeight.getIntValue() + "%");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        long widgetId = bundle.getLong(Widget.ID, -1);

        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();

        super.onCreate(savedInstanceState);
    }

    protected void supportWidgetHeightOption() {
        optionWidgetHeight = loadOrInitOption(WidgetSettingsFragment.WIDGET_HEIGHT);
        updateWidgetHeightText();
    }

}


