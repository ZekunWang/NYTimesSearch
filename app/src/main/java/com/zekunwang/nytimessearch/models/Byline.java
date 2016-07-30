
package com.zekunwang.nytimessearch.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("org.jsonschema2pojo")
public class Byline {

    @SerializedName("organization")
    @Expose
    public String organization;
    @SerializedName("original")
    @Expose
    public String original;

}
