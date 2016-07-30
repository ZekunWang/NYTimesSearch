package com.zekunwang.nytimessearch.interfaces;

import com.zekunwang.nytimessearch.models.NYTimesAPI;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface MyApiEndpointInterface {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("svc/search/v2/articlesearch.json")
    Call<NYTimesAPI> getResponse();
}