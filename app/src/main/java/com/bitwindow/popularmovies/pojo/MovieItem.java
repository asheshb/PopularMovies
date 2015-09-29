package com.bitwindow.popularmovies.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Class to store the movie details parsed from TMDB server response
 */
public class MovieItem{
    private static final String LOG_TAG = MovieItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.


    private Integer id;

    @SerializedName("original_title")
    private String title;

    @SerializedName("poster_path")
    private String posterUrl;

    @SerializedName("backdrop_path")
    private String backdropUrl;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("genre_ids")
    private int[] genre;


    private Double popularity;

    @SerializedName("vote_average")
    private Double userRating;

    @SerializedName("release_date")
    private Date releaseDate;

    @SerializedName("vote_count")
    private Integer voteCount;


    public Integer getId(){
        return id;
    }

    public void setId(Integer id){ this.id = id;}

    public String getTitle(){ return title;}

    public void setTitle(String title){
        this.title = title;
    }

    public String getPosterUrl(String size){ return  posterUrl; }

    public void setPosterUrl(String posterUrl){
        this.posterUrl = posterUrl;
    }

    public String getBackdropUrl(String size){
        return  backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl){
        this.backdropUrl = backdropUrl;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public void setSynopsis(String synopsis){this.synopsis = synopsis;}

    public int[] getGenre(){
        return genre;
    }

    public void setGenre(int[] genre){this.genre = genre;}

    public Double getUserRating(){
        return userRating;
    }

    public void setUserRating(Double userRating){
        this.userRating = userRating;
    }

    public Double getPopularity(){
        return popularity;
    }

    public void setPopularity(Double popularity){
        this.popularity = popularity;
    }

    public Date getReleaseDate(){
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate){ this.releaseDate = releaseDate;}

    public Integer getVoteCount(){ return voteCount; }

    public void setVoteCount(Integer voteCount){ this.voteCount = voteCount; }

}
