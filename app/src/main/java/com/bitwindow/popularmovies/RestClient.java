package com.bitwindow.popularmovies;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by ashbey on 8/20/2015.
 */

public class RestClient {
    // Keep a copy of our API service cached
    private static TMDBApi tmdbApi;
    public static TMDBApi getTMDBApiClient(){
        // Check to see if a sirportlyApiInterface is already instaciated
        if(tmdbApi == null){
            /*
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            */
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(new ItemTypeAdapterFactory()) // This is the important line ;)
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