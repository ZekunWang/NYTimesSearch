package com.zekunwang.nytimessearch.models;

import java.io.Serializable;

/**
 * Created by zwang_000 on 7/21/2016.
 */
public class Date implements Serializable {
    public int year;
    public int month;
    public int day;

    public Date() {}

    public Date(Date date) {
        this.year = date.year;
        this.month = date.month;
        this.day = date.day;
    }

    @Override
    public String toString() {
        if (year == 0) {
            return "Off";
        }
        StringBuilder sb = new StringBuilder();
        if (month < 10) {
            sb.append(0);
        }
        sb.append(month).append('/');
        if (day < 10) {
            sb.append(0);
        }
        sb.append(day).append('/');
        sb.append(year);
        return sb.toString();
    }

    public String toQuery() {
        if (year == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(year);
        if (month < 10) {
            sb.append(0);
        }
        sb.append(month);
        if (day < 10) {
            sb.append(0);
        }
        sb.append(day);
        return sb.toString();
    }
}
