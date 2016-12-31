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

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class WidgetSettingsFragment extends Fragment {
    protected Widget widget;
    protected int scrollViewHeaderLayout = -1;


    public static String WIDGET_HEIGHT = "WIDGET_HEIGHT";
    public static String SCROLL_INTERVAL = "SCROLL_INTERVAL";

    @Nullable
    @BindView(R.id.widget_height)
    TwoLineSettingItem widgetHeight;

    @Nullable
    @BindView(R.id.widget_scroll_interval)
    TwoLineSettingItem scrollInterval;

    // percentage of screen height
    WidgetOption optionWidgetHeight;

    // scroll interval in seconds
    WidgetOption optionScrollInterval;


    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget);
    }

    protected void updateWidgetProperty(String property, Object value) {
        CastCommunicator.sendWidgetProperty(widget, property, value);
    }

    protected WidgetOption loadOrInitOption(String property) {
        return widget.loadOrInitOption(property, getContext());
    }

    @Optional
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

    @Optional
    @OnClick(R.id.widget_scroll_interval)
    public void cycleWidgetScrollInterval() {
        // options are 10, 20, 30, 40
        int currentInterval = optionScrollInterval.getIntValue();
        if (currentInterval >= 40) {
            optionScrollInterval.update(10);
        } else {
            optionScrollInterval.update(currentInterval + 10);
        }
        updateScrollIntervalText();
        updateWidgetProperty(WidgetSettingsFragment.SCROLL_INTERVAL, optionScrollInterval.getIntValue());
    }

    public void updateWidgetHeightText() {
        if (widgetHeight != null)
            widgetHeight.setSubHeaderText(optionWidgetHeight.getIntValue() + "%");
    }


    public void updateScrollIntervalText() {
        if (scrollInterval != null)
            scrollInterval.setSubHeaderText(optionScrollInterval.getIntValue() + " " + getString(R.string.seconds));
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


    protected void supportWidgetScrollInterval() {
        optionScrollInterval = loadOrInitOption(WidgetSettingsFragment.SCROLL_INTERVAL);
        updateScrollIntervalText();
    }

    public boolean hasScrollViewHeader() {
        return scrollViewHeaderLayout != -1;
    }

    protected void setScrollViewHeader(int layoutResource) {
        scrollViewHeaderLayout = layoutResource;
    }

    public int getScrollViewHeader() {
        return scrollViewHeaderLayout;
    }

}


