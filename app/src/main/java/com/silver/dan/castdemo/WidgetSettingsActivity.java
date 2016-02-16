package com.silver.dan.castdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.settingsFragments.CalendarSettings;
import com.silver.dan.castdemo.settingsFragments.ClockSettings;
import com.silver.dan.castdemo.settingsFragments.MapSettings;
import com.silver.dan.castdemo.settingsFragments.PlaceholderSettings;
import com.silver.dan.castdemo.settingsFragments.StocksSettings;
import com.silver.dan.castdemo.settingsFragments.WeatherSettings;
import com.raizlabs.android.dbflow.sql.language.Select;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WidgetSettingsActivity extends AppCompatActivity {

    private Widget widget;

    @Bind(R.id.widget_settings_type_specific)
    FrameLayout widgetTypeSettings;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_settings);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle b = getIntent().getExtras();
        long widgetId = b.getLong(Widget.ID);


        // lookup widget in the database
        // display appropriate settings for that widget type
        widget = new Select().from(Widget.class).where(Widget_Table.id.eq(widgetId)).querySingle();

        setTitle(widget.getHumanName() + " Widget");

        Fragment typeSettingsFragment;
        switch (widget.getWidgetType()) {
            case CALENDAR:
                typeSettingsFragment = new CalendarSettings();
                break;
            case STOCKS:
                typeSettingsFragment = new StocksSettings();
                break;
            case MAP:
                typeSettingsFragment = new MapSettings();
                break;
            case CLOCK:
                typeSettingsFragment = new ClockSettings();
                break;
            case WEATHER:
                typeSettingsFragment = new WeatherSettings();
                break;
            default:
                typeSettingsFragment = new PlaceholderSettings();
        }

        Bundle bundle = new Bundle();
        bundle.putLong(Widget.ID, widget.id);
        typeSettingsFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.widget_settings_type_specific, typeSettingsFragment);

        transaction.commit();
    }

    @OnClick(R.id.widget_settings_delete_button)
    void deleteWidget() {

        new MaterialDialog.Builder(this)
                .title("Delete " + widget.getHumanName())
                .content("Are you sure you want to remove this widget?")
                .positiveText("Yes")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CastCommunicator.deleteWidget(widget);
                        widget.delete();
                        onBackPressed();
                    }
                })
                .show();
    }

}
