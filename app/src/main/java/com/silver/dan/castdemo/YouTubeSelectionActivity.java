package com.silver.dan.castdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
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

    public void searchYouTube(final String queryTerm) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    YouTube youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                        public void initialize(HttpRequest request) throws IOException {
                        }
                    }).setApplicationName(getResources().getString(R.string.app_name)).build();


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

                    final List<YouTubeSearchResult> results = new ArrayList<>();



                    for (SearchResult apiRes : searchResultList) {
                        YouTubeSearchResult result = new YouTubeSearchResult();
                        SearchResultSnippet info = apiRes.getSnippet();
                        result.name = info.getTitle();

                        result.imageUrl = info.getThumbnails().getMedium().getUrl();
                        result.id = apiRes.getId().getVideoId();
                        result.channelTitle = info.getChannelTitle();
                        result.publishedDate = new Date(info.getPublishedAt().getValue());
                        results.add(result);
                    }


//                    ((YouTubeAdapter)searchResultsList.getAdapter()).setList(results);


                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            YouTubeAdapter adapter = new YouTubeAdapter(results);
                            // Attach the adapter to the recyclerview to populate items
                            searchResultsList.setAdapter(adapter);
                            // Set layout manager to position the items
                            searchResultsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        }
                    });

                } catch (GoogleJsonResponseException e) {
                    Log.e(MainActivity.TAG, "There was a service error: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
                } catch (IOException e) {
                    Log.e(MainActivity.TAG, "There was an IO error: " + e.getCause() + " : " + e.getMessage());
                } catch (Throwable t) {
                    Log.e(MainActivity.TAG, t.toString());
                }
            }
        });
    }

    private class YouTubeSearchResult {
        public String name;
        public String id;
        public String imageUrl;
        public String channelTitle;
        public Date publishedDate;
    }

    public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.ViewHolder> {

        private List<YouTubeSearchResult> items;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.youtube_search_result_item, parent, false);

            return new ViewHolder(contactView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            YouTubeSearchResult item = items.get(position);
            holder.nameTextView.setText(item.name);
            holder.tvChannelTitle.setText(item.channelTitle);


            DateFormat df = DateFormat.getDateInstance();
            String dateStr = df.format(item.publishedDate);
            holder.tvPublishedDate.setText(dateStr);

            Picasso.with(getBaseContext())
                    .load(item.imageUrl)
                    .into(holder.ivThumbnail);

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public YouTubeAdapter(List<YouTubeSearchResult> results) {
            this.items = results;
        }


        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView nameTextView;
            public TextView tvChannelTitle;
            public TextView tvPublishedDate;
            public ImageView ivThumbnail;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);

                nameTextView = (TextView) itemView.findViewById(R.id.video_name);
                tvChannelTitle = (TextView) itemView.findViewById(R.id.channel_title);
                tvPublishedDate = (TextView) itemView.findViewById(R.id.video_published_date);
                ivThumbnail = (ImageView) itemView.findViewById(R.id.video_image);

            }
        }
    }
}
