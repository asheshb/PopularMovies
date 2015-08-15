package com.bitwindow.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The fragment which hosts GridView and shows the movie posters in it
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private ArrayList<MovieItem> mMovieListItems;
    private HashMap<Integer,String> mGenres;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String mSortOrder;
    private FetchInternetDataTask mFetchInternetDataTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.movie_items_key))) {
            mMovieListItems = new ArrayList<MovieItem>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mSortOrder = prefs.getString(getString(R.string.sort_order_key),
                    getString(R.string.popular_value));
        }
        else {
            mMovieListItems = savedInstanceState.getParcelableArrayList(getString(R.string.movie_items_key));
            mSortOrder = savedInstanceState.getString(getString(R.string.sort_order_key));
            mGenres = (HashMap<Integer,String>) savedInstanceState.getSerializable(getString(R.string.genres_key));

        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mMovieAdapter =new MovieAdapter(getActivity(), mMovieListItems);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MovieItem movieItem = mMovieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(getString(R.string.movie_item_key), movieItem);
                intent.putExtra(getString(R.string.genres_key), mGenres);
                startActivity(intent);


            }
        });
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridView.setNumColumns(3);
        } else{
            gridView.setNumColumns(2);
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateOptionsMenu()");
        inflater.inflate(R.menu.menu_main_activity_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu (Menu menu){
        if (DEBUG) Log.i(LOG_TAG, "onPrepareOptionsMenu()");
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemPopular = menu.findItem(R.id.action_sort_popular);
        MenuItem menuItemRating = menu.findItem(R.id.action_sort_rating);

        if(mSortOrder.equals(getString(R.string.rating_value))){
            menuItemRating.setEnabled(false);
            menuItemPopular.setEnabled(true);
        } else if(mSortOrder.equals(getString(R.string.popular_value))){
            menuItemPopular.setEnabled(false);
            menuItemRating.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sort_popular || id == R.id.action_sort_rating) {
            if(id == R.id.action_sort_popular) {
                mSortOrder = getString(R.string.popular_value);
            } else if(id == R.id.action_sort_rating) {
                mSortOrder = getString(R.string.rating_value);
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putString(getString(R.string.sort_order_key), mSortOrder).apply();
            updateMovieList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.i(LOG_TAG, "onStart()");
        if(mMovieListItems == null || mMovieListItems.size() <=0) {
            updateMovieList();
        }
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.i(LOG_TAG, "onPause()");
        cancelDataFetch();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(getString(R.string.movie_items_key), mMovieListItems);
        outState.putCharSequence(getString(R.string.sort_order_key), mSortOrder);
        outState.putSerializable(getString(R.string.genres_key), mGenres);
        super.onSaveInstanceState(outState);
    }

    /**
     * Updates the GridView by calling AsyncTask class
     */
    private void updateMovieList() {
        URL url = TMDB.getMovieUrl(mSortOrder);
        if(url != null) {
            cancelDataFetch();
            mFetchInternetDataTask = new FetchInternetDataTask(new FetchMovieItemsTaskCompleteListener());
            mFetchInternetDataTask.execute(url);
        }
        url = TMDB.getGenresUrl();
        //Fill Genres if not already
        if(mGenres == null){
            if (DEBUG) Log.i(LOG_TAG, "updateMovieList() Genres");
            mFetchInternetDataTask = new FetchInternetDataTask(new FetchGenresTaskCompleteListener());
            mFetchInternetDataTask.execute(url);

        }

    }


    /**
     * To stop any previous running tasks
     */
    private void cancelDataFetch(){
        if(mFetchInternetDataTask!=null && mFetchInternetDataTask.getStatus() != AsyncTask.Status.FINISHED) {
            mFetchInternetDataTask.cancel(true);
        }
    }





    /**
     * Handles the callback from AsyncTask for fetching Movie items
     */
    class FetchMovieItemsTaskCompleteListener implements AsyncTaskCompleteListener<String>
    {

        @Override
        public void onTaskComplete(String result)
        {
            if(result != null) {
                try {
                    mMovieListItems = TMDB.getMovieFromJson(result);
                    mMovieAdapter.updateAdapter(mMovieListItems);

                } catch(JSONException e){
                    Toast.makeText(getActivity(), getString(R.string.exception_json),Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, e.getMessage());

                } finally{
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }

        @Override
        public void onTaskBefore()
        {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onExceptionRaised(Exception e) {
            if(e instanceof SocketTimeoutException || e instanceof UnknownHostException){
                Toast.makeText(getActivity(), getString(R.string.exception_no_internet),Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    /**
     * Handles the callback from AsyncTask for fetching Genre list
     */
    class FetchGenresTaskCompleteListener implements AsyncTaskCompleteListener<String>
    {

        @Override
        public void onTaskComplete(String result)
        {
            if(mGenres ==null && result != null) {
                try {
                    mGenres = TMDB.getGenresFromJson(result);
                } catch(JSONException e){
                    //As this is non crucial feature don't show it to user
                    Log.d(LOG_TAG, e.getMessage());

                }
            }
        }

        @Override
        public void onTaskBefore()
        {

        }

        @Override
        public void onExceptionRaised(Exception e) {
            //As this is non crucial feature don;t show it to user
        }
    }


//FOLLOWING FUNCTIONS FOR DEBUGGING PURPOSE ONLY.

    @Override
    public void onResume() {
        if (DEBUG) Log.i(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.i(LOG_TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(LOG_TAG, "onAttach()");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        if (DEBUG) Log.i(LOG_TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }
}
