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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RSSSettings extends WidgetSettingsFragment {

    @Bind(R.id.feed_url)
    TwoLineSettingItem feedUrl;

    WidgetOption feedUrlOption;

    public static String FEED_URL = "FEED_URL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rss_settings, container, false);
        ButterKnife.bind(this, view);

        feedUrlOption = loadOrInitOption(RSSSettings.FEED_URL);
        updateFeedURLText();
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
                        feedUrlOption.value = input.toString();
                        feedUrlOption.save();
                        updateFeedURLText();
                        refreshWidget();
                    }
                }).show();
    }
}