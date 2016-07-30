
package com.zekunwang.nytimessearch.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Response {

    @SerializedName("docs")
    @Expose
    public List<Doc> docs = new ArrayList<Doc>();

}
