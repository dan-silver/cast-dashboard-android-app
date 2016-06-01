package com.silver.dan.castdemo.settingsFragments;

/**
 * Created by dan on 5/29/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.silver.dan.castdemo.DelayAutoCompleteTextView;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;
import com.silver.dan.castdemo.YouTubeSelectionActivity;
import com.silver.dan.castdemo.youtube.OnVideoClickListener;
import com.silver.dan.castdemo.youtube.YouTubeVideo;
import com.silver.dan.castdemo.youtube.YouTubeVideoListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class YouTubeSettings extends WidgetSettingsFragment {

    @Bind(R.id.video)
    DelayAutoCompleteTextView videoSearchInput;

    @Bind(R.id.youtube_playlist)
    RecyclerView playlist;


    WidgetOption playlistDetails;


    // for storing in db
    public static String PLAYLIST_DETAILS = "PLAYLIST_DETAILS";

    // for getting results from search activity in data bundles
    public static final String VIDEO_ID = "VIDEO_ID";
    public static final String VIDEO_NAME = "VIDEO_NAME";
    public static final String VIDEO_IMG_URL = "VIDEO_IMG_URL";
    public static final String VIDEO_DATE = "VIDEO_DATE";
    public static final String VIDEO_CHANNEL = "VIDEO_CHANNEL";


    private int SEARCH_YOUTUBE_REQUEST = 1;
    private AutoCompleteAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.youtube_settings, container, false);
        ButterKnife.bind(this, view);

        playlistDetails = loadOrInitOption(PLAYLIST_DETAILS);

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


        YouTubeVideoListAdapter adapter = new YouTubeVideoListAdapter(getPlaylist(), new OnVideoClickListener() {
            @Override
            public void onClick(YouTubeVideo video) {
                //@todo
            }
        });
        // Attach the adapter to the recyclerview to populate items
        playlist.setAdapter(adapter);
        // Set layout manager to position the items
        playlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    public void savePlaylist() {
        List<YouTubeVideo> videos = ((YouTubeVideoListAdapter) playlist.getAdapter()).getVideos();
        JSONArray videoArr = new JSONArray();
        for (YouTubeVideo video : videos) {
            videoArr.put(video.convertToJSON());
        }
        playlistDetails.update(videoArr.toString());
    }

    public List<YouTubeVideo> getPlaylist() {
        ArrayList<YouTubeVideo> videos = new ArrayList<>();
        try {
            JSONArray jsonarray = new JSONArray(playlistDetails.value);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                videos.add(YouTubeVideo.createFromJson(jsonobject));
            }
        } catch (JSONException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
        return videos;
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
                String videoName = data.getStringExtra(VIDEO_NAME);
                String videoImgUrl = data.getStringExtra(VIDEO_IMG_URL);
                long videoDate = data.getLongExtra(VIDEO_DATE, 0);
                String channelName = data.getStringExtra(VIDEO_CHANNEL);

                YouTubeVideo video = new YouTubeVideo();
                video.id = videoId;
                video.name = videoName;
                video.imageUrl = videoImgUrl;
                video.publishedDate = new Date(videoDate);
                video.channelTitle = channelName;

//                updateWidgetProperty(VIDEO_ID, videoId);

                ((YouTubeVideoListAdapter) playlist.getAdapter()).addVideo(video);
                savePlaylist();

                videoSearchInput.setText("");
            }
        }
    }

}