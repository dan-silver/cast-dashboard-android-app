package com.silver.dan.castdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.silver.dan.castdemo.settingsFragments.YouTubeSettings;
import com.silver.dan.castdemo.youtube.OnVideoClickListener;
import com.silver.dan.castdemo.youtube.YouTubeVideo;
import com.silver.dan.castdemo.youtube.YouTubeVideoListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class YouTubeSelectionActivity extends AppCompatActivity {

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    @Bind(R.id.youtube_search_results)
    RecyclerView searchResultsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_selection);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String q = extras.getString("q");
            searchYouTube(q);
        }

    }

    private class SearchYouTubeTask extends AsyncTask<String, String, List<YouTubeVideo>> {

        @Override
        protected List<YouTubeVideo> doInBackground(String... params) {
            YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(getResources().getString(R.string.app_name)).build();


            // Define the API request for retrieving search results.
            YouTube.Search.List search;
            try {
                search = youtube.search().list("id,snippet");
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.toString());
                return new ArrayList<>();
            }

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            String apiKey = getString(R.string.BrowserKey1);
            search.setKey(apiKey);
            search.setQ(params[0]);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            search.getRequestHeaders().set("referer", getString(R.string.browserRefererSpoof));
            SearchListResponse searchResponse;
            try {
                searchResponse = search.execute();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, "There was an IO error: " + e.getCause() + " : " + e.getMessage());
                return new ArrayList<>();
            }
            List<SearchResult> searchResultList = searchResponse.getItems();

            final List<YouTubeVideo> results = new ArrayList<>();



            for (SearchResult apiRes : searchResultList) {
                YouTubeVideo result = new YouTubeVideo();
                SearchResultSnippet info = apiRes.getSnippet();
                result.name = info.getTitle();

                result.imageUrl = info.getThumbnails().getMedium().getUrl();
                result.id = apiRes.getId().getVideoId();
                result.channelTitle = info.getChannelTitle();
                result.publishedDate = new Date(info.getPublishedAt().getValue());
                results.add(result);
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<YouTubeVideo> results) {
            YouTubeVideoListAdapter adapter = new YouTubeVideoListAdapter(results, new OnVideoClickListener() {
                @Override
                public void onClick(YouTubeVideo video) {
                    Intent data = new Intent();

                    data.putExtra(YouTubeSettings.VIDEO_ID, video.id);
                    data.putExtra(YouTubeSettings.VIDEO_NAME, video.name);
                    data.putExtra(YouTubeSettings.VIDEO_IMG_URL, video.imageUrl);
                    data.putExtra(YouTubeSettings.VIDEO_DATE, video.publishedDate.getTime());
                    data.putExtra(YouTubeSettings.VIDEO_CHANNEL, video.channelTitle);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
            // Attach the adapter to the recyclerview to populate items
            searchResultsList.setAdapter(adapter);
            // Set layout manager to position the items
            searchResultsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }

        @Override
        protected void onPreExecute() {}
    }

    public void searchYouTube(final String queryTerm) {
        new SearchYouTubeTask().execute(queryTerm);
    }


}
