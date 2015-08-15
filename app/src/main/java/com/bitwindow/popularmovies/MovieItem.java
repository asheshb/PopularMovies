package com.bitwindow.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to store the movie details
 */
class MovieItem implements Parcelable {
    private static final String LOG_TAG = MovieItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private Integer id;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private String synopsis;
    private Double userRating;
    private String releaseDate;
    private Integer voteCount;
    private int[] genres;

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){ this.id = id;}

    public String getTitle(){ return title;}

    public void setTitle(String title){
        this.title = title;
    }

    public String getPosterUrl(String size){ return TMDB.getImageBaseUrl(size) + posterUrl; }

    public void setPosterUrl(String posterUrl){
        this.posterUrl = posterUrl;
    }

    public String getBackdropUrl(String size){
        return TMDB.getImageBaseUrl(size) + backdropUrl;
    }

    public void setBackdropUrl(String backdropUrl){
        this.backdropUrl = backdropUrl;
    }

    public String getSynopsis(){
        return synopsis;
    }

    public void setSynopsis(String synopsis){this.synopsis = synopsis;}

    public Double getUserRating(){
        return userRating;
    }

    public void setUserRating(Double userRating){
        this.userRating = userRating;
    }

    public String getReleaseDate(){
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate){ this.releaseDate = releaseDate;}

    public Integer getVoteCount(){ return voteCount; }

    public void setVoteCount(Integer voteCount){ this.voteCount = voteCount; }

    public int[] getGenres(){ return genres; }

    public void setGenres(int[] genres){ this.genres= genres; }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.posterUrl);
        dest.writeString(this.backdropUrl);
        dest.writeString(this.synopsis);
        dest.writeValue(this.userRating);
        dest.writeString(this.releaseDate);
        dest.writeInt(this.voteCount);
        dest.writeIntArray(this.genres);
    }

    public MovieItem() {
    }

    private MovieItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.posterUrl = in.readString();
        this.backdropUrl = in.readString();
        this.synopsis = in.readString();
        this.userRating = (Double) in.readValue(Double.class.getClassLoader());
        this.releaseDate = in.readString();
        this.voteCount = in.readInt();
        this.genres = in.createIntArray();
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
