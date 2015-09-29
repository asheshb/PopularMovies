package com.bitwindow.popularmovies.api;

import com.bitwindow.popularmovies.TMDB;
import com.bitwindow.popularmovies.pojo.GenreItem;
import com.bitwindow.popularmovies.pojo.MovieItem;
import com.bitwindow.popularmovies.pojo.ReviewItem;
import com.bitwindow.popularmovies.pojo.VideoItem;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by ashbey on 8/19/2015.
 * This class is used to define various uri to fetch data from TMDB server
 */
public interface TMDBApi {
    @GET("/discover/movie")
    void getMovies(@Query(TMDB.SORT_PARAM) String sortBy, @Query(TMDB.MIN_VOTE_PARAM) String minVote, @Query(TMDB.API_KEY_PARAM) String key,
                    Callback<List<MovieItem>> callback);


    @GET("/movie/{id}/reviews")
    void getReviews(@Path(TMDB.ID_PARAM) String id, @Query(TMDB.API_KEY_PARAM) String key,
                   Callback<List<ReviewItem>> callback);

    @GET("/movie/{id}/videos")
    void getVideos(@Path(TMDB.ID_PARAM) String id, @Query(TMDB.API_KEY_PARAM) String key,
                    Callback<List<VideoItem>> callback);

    @GET("/genre/movie/list")
    void getGenre(@Query(TMDB.API_KEY_PARAM) String key,
                   Callback<List<GenreItem>> callback);


}

