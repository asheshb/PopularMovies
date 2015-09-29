package com.bitwindow.popularmovies.api;

import com.bitwindow.popularmovies.TMDB;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by ashbey on 8/20/2015.
 * This class is used to fetch data from TMDB server
 */

public class RestClient {
    // Keep a copy of our API service cached
    private static TMDBApi tmdbApi;
    public static TMDBApi getTMDBApiClient(){

        if(tmdbApi == null){
            /*
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            */
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(TMDB.BASE_URL)
                    .setConverter(new GsonConverter(gson))
                    .build();
            tmdbApi = restAdapter.create(TMDBApi.class);
        }
        return tmdbApi;
    }
}