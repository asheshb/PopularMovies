package com.bitwindow.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * The main activity to show movie posters
 */

public class MainActivity extends AppCompatActivity implements MainActivityFragment.ClickCallback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_container, new MovieDetailsFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    public boolean isTwoPane(){
        return mTwoPane;

    }

    /**
     * Call back from MainActivityFragment to launch the movie details in fragment or activity
     * @param contentUri uri of the movie detail
     */
    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailsFragment.DETAIL_URI, contentUri);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    //FOLLOWING FUNCTIONS FOR DEBUGGING PURPOSE ONLY.
    @Override
    protected void onStart() {
        if (DEBUG) Log.i(LOG_TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (DEBUG) Log.i(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (DEBUG) Log.i(LOG_TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (DEBUG) Log.i(LOG_TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }
}
