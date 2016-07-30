package com.zekunwang.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Random;

@Parcel
public class Article {

    public String webUrl;
    public String headLine;
    public String thumbNail;
    public String snippet;

    public Article() {}

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                results.add(new Article(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headLine = jsonObject.getJSONObject("headline").getString("main");
            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                // pick random thumbnail for staggered layout
                int index = new Random().nextInt(multimedia.length());
                this.thumbNail = "http://www.nytimes.com/" + multimedia.getJSONObject(index).getString("url");
            }
            this.snippet = jsonObject.getString("snippet");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}