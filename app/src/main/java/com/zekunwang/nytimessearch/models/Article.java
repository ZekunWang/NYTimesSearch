package com.zekunwang.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zwang_000 on 7/17/2016.
 */
public class Article implements Serializable {

    String webUrl;
    String headLine;
    String thumbNail;

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
                this.thumbNail = "http://www.nytimes.com/" + multimedia.getJSONObject(0).getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public String getThumbNail() {
        return thumbNail;
    }
}
