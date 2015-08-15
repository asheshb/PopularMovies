package com.bitwindow.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
    }



}
