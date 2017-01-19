package com.silver.dan.castdemo.settingsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class IFrameSettings extends WidgetSettingsFragment {

    @BindView(R.id.iframe_url)
    EditText iframeURL;

    WidgetOption optionFrameURL;

    public static String URL = "URL";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.iframe_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void initView() {
        optionFrameURL = loadOrInitOption(IFrameSettings.URL);

        iframeURL.setText(optionFrameURL.value);

        supportWidgetHeightOption();
        supportWidgetRefreshInterval();

    }

    @OnTextChanged(R.id.iframe_url)
    public void updateFrameUrl() {
        String frameUrl = iframeURL.getText().toString();
        optionFrameURL.update(frameUrl);
        updateWidgetProperty(URL, frameUrl);
    }
}