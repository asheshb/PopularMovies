package com.bitwindow.popularmovies;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ashbey on 8/19/2015.
 */
public interface TMDBApi {
    @GET("/discover/movie")
    void getMovies(@Query(TMDB.SORT_PARAM) String sortBy, @Query(TMDB.MIN_VOTE_PARAM) String minVote, @Query(TMDB.API_KEY_PARAM) String key,
                    Callback<List<MovieItem>> callback);


    @GET("/genre/movie/list")
    void getGenres(@Query(TMDB.API_KEY_PARAM) String key,
                   Callback<List<GenreItem>> callback);
}

