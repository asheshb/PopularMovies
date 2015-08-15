package com.bitwindow.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            if (DEBUG) Log.i(LOG_TAG, "New Fragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainActivityFragment())
                    .commit();

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
