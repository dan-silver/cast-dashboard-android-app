package com.silvr.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.silvr.dan.castdemo.CastCommunicator;
import com.silvr.dan.castdemo.MainActivity;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.silvr.dan.castdemo.Widget_Table;
import com.raizlabs.android.dbflow.sql.language.Select;

public class WidgetSettingsFragment extends Fragment {
    protected Widget widget;

    protected void refreshWidget() {
        CastCommunicator.sendWidget(widget);
    }

    protected void updateWidgetProperty(String property, Object value) {
        CastCommunicator.sendWidgetProperty(widget, property, value);
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


    protected WidgetOption loadOption(String optionKey) {
        WidgetOption option = widget.getOption(optionKey);

        if (option != null) {
            return option;
        }

        // this might be a new version of the app that doesn't have this option yet
        // that's fine, pretend like we're creating this widget for the first time (non-destructive for existing saved options)

        init(this.widget);

        option = widget.getOption(optionKey);
        if (option == null) {
            Log.e(MainActivity.TAG, "Trying to access option that doesn't exist!" + optionKey);
        }

        return option;

    }

}


