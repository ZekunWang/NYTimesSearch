
package com.zekunwang.nytimessearch.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("org.jsonschema2pojo")
public class Headline {

    @SerializedName("main")
    @Expose
    public String main;
    @SerializedName("kicker")
    @Expose
    public String kicker;

}
