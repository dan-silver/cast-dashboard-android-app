package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RSSSettings extends WidgetSettingsFragment {

    @BindView(R.id.feed_url)
    TwoLineSettingItem feedUrl;

    @BindView(R.id.display_rss_dates)
    Switch displayDates;

    WidgetOption feedUrlOption, showDatesOption;

    public static String FEED_URL = "FEED_URL";
    public static String SHOW_DATES = "SHOW_DATES";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rss_settings, container, false);
        ButterKnife.bind(this, view);
        initView();

        return view;
    }


    public void updateFeedURLText() {
        if (feedUrlOption.value.length() > 0) {
            feedUrl.setSubHeaderText(feedUrlOption.value);
        } else {
            feedUrl.setSubHeaderText(R.string.no_rss_feed_url_set);
        }
    }

    @OnClick(R.id.feed_url)
    public void setFeedUrl() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.feed_url)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                .input(getString(R.string.feed_url), feedUrlOption.value, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        feedUrlOption.update(input.toString());
                        updateWidgetProperty(RSSSettings.FEED_URL, feedUrlOption);
                        updateFeedURLText();
                        refreshWidget();
                    }
                }).show();
    }

    @Override
    public void initView() {
        feedUrlOption = loadOrInitOption(RSSSettings.FEED_URL);
        showDatesOption = loadOrInitOption(RSSSettings.SHOW_DATES);

        supportWidgetHeightOption();
        supportWidgetScrollInterval();
        supportWidgetRefreshInterval();
        updateFeedURLText();

        displayDates.setChecked(showDatesOption.getBooleanValue());
        displayDates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDatesOption.update(isChecked);
                updateWidgetProperty(RSSSettings.SHOW_DATES, showDatesOption.getBooleanValue());
            }
        });

    }
}