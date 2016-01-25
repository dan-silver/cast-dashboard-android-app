package com.example.dan.castdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.dan.castdemo.settingsFragments.CalendarSettings;
import com.example.dan.castdemo.settingsFragments.PlaceholderSettings;
import com.example.dan.castdemo.settingsFragments.StocksSettings;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetSettings extends Fragment {


    private Widget widget;
    long widgetId;

    @Bind(R.id.widget_settings_title)
    TextView widgetSettingsTitle;

    @Bind(R.id.widget_settings_type_specific)
    FrameLayout widgetTypeSettings;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        widgetId = bundle.getLong(Widget.ID, -1);

        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();


        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_settings, container, false);
        ButterKnife.bind(this, view);

        widgetSettingsTitle.setText(widget.getHumanName() + " Widget");

        Fragment typeSettingsFragment;

        if (widget.getWidgetType() == Widget.types.CALENDAR) {
            typeSettingsFragment = new CalendarSettings();
        } else if (widget.getWidgetType() == Widget.types.STOCKS){
            typeSettingsFragment = new StocksSettings();
        } else {
            typeSettingsFragment = new PlaceholderSettings();
        }


        Bundle bundle = new Bundle();
        bundle.putLong(Widget.ID, widget.id);
        typeSettingsFragment.setArguments(bundle);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.widget_settings_type_specific, typeSettingsFragment);

        transaction.commit();


        return view;
    }

    @OnClick(R.id.widget_settings_delete_button)
    void deleteWidget() {

        new MaterialDialog.Builder(getContext())
                .title("Delete " + widget.getHumanName())
                .content("Are you sure you want to remove this widget?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        widget.delete();
                        getActivity().onBackPressed();
                    }
                })
                .show();
    }
}