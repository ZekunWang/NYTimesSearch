package com.zekunwang.nytimessearch.models;

import com.loopj.android.http.RequestParams;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by zwang_000 on 7/21/2016.
 */
public class Setting implements Serializable {
    public Date beginDate;
    public int sort;
    public boolean art;
    public boolean fashion;
    public boolean sports;

    public Setting() {
        beginDate = new Date();
    }

    public Setting(Setting setting) {
        this.beginDate = new Date(setting.beginDate);
        this.sort = setting.sort;
        this.art = setting.art;
        this.fashion = setting.fashion;
        this.sports = setting.sports;
    }

    public RequestParams getQuery() {
        RequestParams param = new RequestParams();
        if (beginDate.year != 0) {
            param.put("begin_date", beginDate.toQuery());  // set begin date query
        }
        if (sort != 0) {
            param.put("sort", (sort == 1 ? "newest" : "oldest"));    // set sort query
        }
        // add art fashion and sport
        Log.i("DEBUG", "no art, fashion, sports yet");
        return param;
    }
}
