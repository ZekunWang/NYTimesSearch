
package com.zekunwang.nytimessearch.models;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("org.jsonschema2pojo")
public class Multimedium {

    @SerializedName("url")
    @Expose
    public String url;

    public Multimedium() {}

    /**
     * 
     * @return
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

}
