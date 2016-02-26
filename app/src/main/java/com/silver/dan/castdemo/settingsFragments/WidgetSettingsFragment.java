package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.silver.dan.castdemo.CastCommunicator;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

public abstract class WidgetSettingsFragment extends Fragment {
    protected Widget widget;

    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget);
    }

    protected void updateWidgetProperty(String property, Object value) {
        CastCommunicator.sendWidgetProperty(widget, property, value);
    }

    protected WidgetOption loadOrInitOption(String showSeconds) {
        return widget.loadOrInitOption(showSeconds, getContext());
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

}


