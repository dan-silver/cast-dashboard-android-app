package com.silver.dan.castdemo.settingsFragments;

/**
 * Created by dan on 5/29/16.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.WidgetOption;

import java.io.IOException;
import java.util.List;

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

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;


    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Define a global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();


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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // This object is used to make YouTube Data API requests. The last
                    // argument is required, but since we don't need anything
                    // initialized when the HttpRequest is initialized, we override
                    // the interface and provide a no-op function.
                    YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                        public void initialize(HttpRequest request) throws IOException {
                        }
                    }).setApplicationName(getResources().getString(R.string.app_name)).build();

                    // Prompt the user to enter a query term.
                    String queryTerm = "doctor who river song";

                    // Define the API request for retrieving search results.
                    YouTube.Search.List search = youtube.search().list("id,snippet");

                    // Set your developer key from the {{ Google Cloud Console }} for
                    // non-authenticated requests. See:
                    // {{ https://cloud.google.com/console }}
                    String apiKey = getString(R.string.BrowserKey1);
                    search.setKey(apiKey);
                    search.setQ(queryTerm);

                    // Restrict the search results to only include videos. See:
                    // https://developers.google.com/youtube/v3/docs/search/list#type
                    search.setType("video");

                    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

                    // Call the API and print results.
                    search.getRequestHeaders().set("referer", getString(R.string.browserRefererSpoof));
                    SearchListResponse searchResponse = search.execute();
                    List<SearchResult> searchResultList = searchResponse.getItems();


                    Log.v("a", searchResultList.toString());

                } catch (GoogleJsonResponseException e) {
                    System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
                } catch (IOException e) {
                    System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }
}