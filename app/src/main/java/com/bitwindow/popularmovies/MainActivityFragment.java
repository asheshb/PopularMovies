package com.bitwindow.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The fragment which hosts GridView and shows the movie posters in it
 */
public class MainActivityFragment extends Fragment {
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private ArrayList<MovieItem> mMovieListItems;
    private ArrayList<GenreItem> mGenreList;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private String mSortOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.movie_items_key))) {
            mMovieListItems = new ArrayList<MovieItem>();
            mGenreList = new ArrayList<GenreItem>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mSortOrder = prefs.getString(getString(R.string.sort_order_key),
                    getString(R.string.popular_value));
        }
        else {
            mMovieListItems = savedInstanceState.getParcelableArrayList(getString(R.string.movie_items_key));
            mGenreList = savedInstanceState.getParcelableArrayList(getString(R.string.genres_key));
            mSortOrder = savedInstanceState.getString(getString(R.string.sort_order_key));

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
                intent.putExtra("genres_text", getGenreString(movieItem.getGenres()));
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
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList(getString(R.string.movie_items_key), mMovieListItems);
        outState.putParcelableArrayList(getString(R.string.genres_key), mGenreList);
        outState.putCharSequence(getString(R.string.sort_order_key), mSortOrder);
        super.onSaveInstanceState(outState);
    }

    /**
     * Updates the GridView by calling AsyncTask class
     */
    private void updateMovieList() {
        String sortOrder;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        if(mSortOrder.equals(getString(R.string.rating_value))){
            sortOrder = TMDB.SORT_RATING;
        } else{
            sortOrder = TMDB.SORT_POPULAR;
        }

        RestClient.getTMDBApiClient().getMovies(sortOrder, TMDB.MIN_VOTE, TMDB.API_KEY, new Callback<List<MovieItem>>()
            {
                @Override
                public void success(List<MovieItem> movieItems, Response response)
                {
                    mMovieListItems = (ArrayList<MovieItem>) movieItems;
                    mMovieAdapter.updateAdapter(mMovieListItems);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }

                @Override
                public void failure(RetrofitError error)
                {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Error : " + error.getMessage());
                }
            });

        if(mGenreList == null || mGenreList.size()<=0){

            RestClient.getTMDBApiClient().getGenres(TMDB.API_KEY, new Callback<List<GenreItem>>() {
                @Override
                public void success(List<GenreItem> genreItems, Response response) {
                    mGenreList = (ArrayList<GenreItem>) genreItems;
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(LOG_TAG, "Error getGenres : " + error.getMessage());
                }
            });


        }
    }


    private String getGenreString(int[] genres){
        if(mGenreList == null || mGenreList.size()<0)
            return "";
        int id;
        List<String> genresText =  new ArrayList<String>();;
        for(GenreItem d : mGenreList){
            for (int i : genres) {
                if(d.getId() == i){
                    genresText.add(d.getName());
                }
            }
        }
        return TextUtils.join(", ", genresText);
    }

//FOLLOWING FUNCTIONS FOR DEBUGGING PURPOSE ONLY.

    @Override
    public void onResume() {
        if (DEBUG) Log.i(LOG_TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.i(LOG_TAG, "onPause()");
        super.onPause();
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
