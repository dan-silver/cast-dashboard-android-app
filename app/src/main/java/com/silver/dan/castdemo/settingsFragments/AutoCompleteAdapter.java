package com.silver.dan.castdemo.settingsFragments;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dan on 5/29/16.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> mData;

    public AutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        mData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int index) {
        return mData.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    mData.clear();
                    // A class that queries a web API, parses the data and returns an ArrayList<Style>

                    OkHttpClient client = new OkHttpClient();


                    Request request = new Request.Builder()
                            .url("http://suggestqueries.google.com/complete/search?callback=?&hl=en&client=firefox&q="+constraint)
                            .build();

                    Response response;
                    try {
                        response = client.newCall(request).execute();
                        JSONArray mainObject = new JSONArray(response.body().string());
                        JSONArray keywords = mainObject.getJSONArray(1);

                        int length = keywords.length();
                        for (int i=0; i<length; i++) {
                            mData.add(keywords.getString(i));
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    // Now assign the values and count to the FilterResults object
                    filterResults.values = mData;
                    filterResults.count = mData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

}