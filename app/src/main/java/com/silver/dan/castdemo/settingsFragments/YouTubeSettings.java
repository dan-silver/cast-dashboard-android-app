package com.silver.dan.castdemo.settingsFragments;

/**
 * Created by dan on 5/29/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.silver.dan.castdemo.DelayAutoCompleteTextView;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.YouTubeSelectionActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class YouTubeSettings extends WidgetSettingsFragment {


    @Bind(R.id.video)
    DelayAutoCompleteTextView videoSearchInput;

    WidgetOption selectedVideoId;
    WidgetOption selectedVideoName;

    public static String VIDEO_ID = "VIDEO_ID";
    public static String CACHED_VIDEO_NAME = "VIDEO_NAME";
    private int SEARCH_YOUTUBE_REQUEST = 1;
    private AutoCompleteAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.youtube_settings, container, false);
        ButterKnife.bind(this, view);

        selectedVideoId   = loadOrInitOption(VIDEO_ID);
        selectedVideoName = loadOrInitOption(CACHED_VIDEO_NAME);

        videoSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    videoSearch(videoSearchInput.getText().toString());
                    return true;
                }
                return false;
            }
        });


        adapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line);
        videoSearchInput.setAdapter(adapter);

        videoSearchInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String query = adapter.getItem(position);
                videoSearch(query);

            }
        });

        // if the video hasn't been set yet, focus on the search bar
        if (selectedVideoId.value.equals("")) {
//            videoSearchInput.requestFocus();
//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


//            ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
//                    .showSoftInput(videoSearchInput, InputMethodManager.SHOW_FORCED);
//            this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        }

        return view;
    }


    public void videoSearch(String query) {
        if (query == null || query.length() < 4) {
            Toast.makeText(getContext(), getString(R.string.try_searching_again), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), YouTubeSelectionActivity.class);
        intent.putExtra("q", query);
        startActivityForResult(intent, SEARCH_YOUTUBE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SEARCH_YOUTUBE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                String videoId = data.getStringExtra(VIDEO_ID);
                String videoName = data.getStringExtra(CACHED_VIDEO_NAME);

                selectedVideoId.update(videoId);
                selectedVideoName.update(videoName);

                updateWidgetProperty(VIDEO_ID, videoId);

                videoSearchInput.setText("");
            }
        }
    }

}