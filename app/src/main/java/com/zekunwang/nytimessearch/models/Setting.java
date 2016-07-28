package com.zekunwang.nytimessearch.models;

import com.loopj.android.http.RequestParams;

import org.parceler.Parcel;

@Parcel
public class Setting {
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
        if (art || fashion || sports) {
            StringBuilder sb = new StringBuilder("news_desk:(");
            if (art) {
                sb.append("\"Arts\"");
            }
            if (fashion) {
                if (sb.length() > 11) {
                    sb.append(',');
                }
                sb.append("\"Fashion & Style\"");
            }
            if (sports) {
                if (sb.length() > 11) {
                    sb.append(',');
                }
                sb.append("\"Sports\"");
            }
            sb.append(')');
            param.put("fq", sb.toString());
        }
        return param;
    }
}
