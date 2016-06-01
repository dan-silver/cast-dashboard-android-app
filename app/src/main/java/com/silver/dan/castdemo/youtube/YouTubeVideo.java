package com.silver.dan.castdemo.youtube;

import android.util.Log;

import com.silver.dan.castdemo.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by dan on 5/31/16.
 */
public class YouTubeVideo {
    public String name;
    public String id;
    public String imageUrl;
    public String channelTitle;
    public Date publishedDate;

    public JSONObject convertToJSON() {
        JSONObject v = new JSONObject();
        try {
            v.put("name", name);
            v.put("id", id);
            v.put("imageUrl", imageUrl);
            v.put("channelTitle", channelTitle);
            v.put("publishedDate", publishedDate.getTime());
        } catch (JSONException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
        return v;
    }

    public static YouTubeVideo createFromJson(JSONObject o) {
        YouTubeVideo v = new YouTubeVideo();
        try {
            v.name = o.getString("name");
            v.id   = o.getString("id");
            v.imageUrl = o.getString("imageUrl");
            v.channelTitle = o.getString("channelTitle");
            v.publishedDate = new Date(o.getLong("publishedDate"));
        } catch (JSONException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
        return v;
    }
}