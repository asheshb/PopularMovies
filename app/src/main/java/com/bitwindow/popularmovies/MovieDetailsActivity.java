package com.bitwindow.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * The activity to host movie details fragment
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState == null) {
            if (DEBUG) Log.i(LOG_TAG, "New Fragment");
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.DETAIL_URI, getIntent().getData());

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();

        }


    }



}
