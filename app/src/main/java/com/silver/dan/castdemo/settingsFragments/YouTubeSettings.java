package com.silver.dan.castdemo.settingsFragments;

/**
 * Created by dan on 5/29/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.YouTubeSelectionActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class YouTubeSettings extends WidgetSettingsFragment {

    @Bind(R.id.video)
    TwoLineSettingItem selectedVideo;

    WidgetOption selectedVideoId;
    WidgetOption selectedVideoName;

    public static String VIDEO_ID = "VIDEO_ID";
    public static String CACHED_VIDEO_NAME = "VIDEO_NAME";
    private int SEARCH_YOUTUBE_REQUEST = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.youtube_settings, container, false);
        ButterKnife.bind(this, view);

        selectedVideoId   = loadOrInitOption(VIDEO_ID);
        selectedVideoName = loadOrInitOption(CACHED_VIDEO_NAME);

        updateVideoNameText();

        return view;
    }

    public void updateVideoNameText() {
        selectedVideo.setSubHeaderText(selectedVideoName.value);
    }

    @OnClick(R.id.video)
    public void selectVideo() {
        Intent intent = new Intent(getContext(), YouTubeSelectionActivity.class);
        intent.putExtra("q", "star trek trailer"); //@todo
        startActivityForResult(intent, SEARCH_YOUTUBE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SEARCH_YOUTUBE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

}