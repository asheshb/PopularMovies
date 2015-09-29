package com.bitwindow.popularmovies.pojo;

/**
 * Class to store the genre details parsed from TMDB server response
 */
public class GenreItem {
    private static final String LOG_TAG = GenreItem.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.



    private int id;
    private String name;



    public int getId(){ return id;}

    public void setId(int id){
        this.id = id;
    }

    public String getName(){ return name; }

    public void setName(String name){
        this.name = name;
    }

    private GenreItem() {
    }

}
