package com.bitwindow.popularmovies;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

import com.bitwindow.popularmovies.adapter.MovieListAdapter;
import com.bitwindow.popularmovies.api.RestClient;
import com.bitwindow.popularmovies.data.MovieContract;
import com.bitwindow.popularmovies.pojo.GenreItem;
import com.bitwindow.popularmovies.pojo.MovieItem;

import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The fragment which hosts GridView and shows the movie posters in it
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private static final int MOVIE_LOADER = 0;
    // For the movie view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
    };

    private static final int COL_MOVIE_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_POSTER_URL = 2;
    public static final int COL_VOTE_AVG = 3;


    private MovieListAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private GridView mGridView;


    //Sore which list is current showing. popular, rating or favorite
    private int mCurrentList;
    private static final String CURRENT_LIST_KEY = "list";



    //To store the position of grid view item and restore when changing orientation
    private int mItemPosition;
    private static final String ITEM_POSITION_KEY = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG){
            Log.i(LOG_TAG, "onCreate()");
            //Picasso
            //        .with(getActivity())
            //        .setIndicatorsEnabled(true);
        }

        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mCurrentList = prefs.getInt(CURRENT_LIST_KEY,
                    Utility.POPULAR);
            mItemPosition = 0;
        }
        else {
            if(savedInstanceState.containsKey(CURRENT_LIST_KEY)) {
                mCurrentList = savedInstanceState.getInt(CURRENT_LIST_KEY);
            }
            if(savedInstanceState.containsKey(ITEM_POSITION_KEY)) {
                mItemPosition = savedInstanceState.getInt(ITEM_POSITION_KEY);
            }

        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mMovieAdapter =new MovieListAdapter(getActivity(), null, 0);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);


        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (null != cursor) {
                    if(Utility.FAVORITE == mCurrentList){
                        ((ClickCallback) getActivity()).onItemSelected(MovieContract.FavoriteEntry.buildFavoriteUri(cursor.getLong(COL_MOVIE_ID)));
                    } else {
                        ((ClickCallback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)));
                    }
                    mItemPosition = position;
                }
            }
        });


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mGridView.setNumColumns(3);
        } else{
            mGridView.setNumColumns(2);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            updateMovieList();
            updateGenreList();
        }
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
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

        MenuItem menuItemPopular = menu.findItem(R.id.action_list_popular);
        MenuItem menuItemRating = menu.findItem(R.id.action_list_rating);
        MenuItem menuItemFavorite = menu.findItem(R.id.action_list_favorite);

        if(mCurrentList == Utility.RATING){
            menuItemRating.setEnabled(false);
            menuItemPopular.setEnabled(true);
            menuItemFavorite.setEnabled(true);

        } else if(mCurrentList == Utility.POPULAR){
            menuItemPopular.setEnabled(false);
            menuItemRating.setEnabled(true);
            menuItemFavorite.setEnabled(true);
        } else if(mCurrentList == Utility.FAVORITE){
            menuItemFavorite.setEnabled(false);
            menuItemPopular.setEnabled(true);
            menuItemRating.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_list_popular || id == R.id.action_list_rating || id == R.id.action_list_favorite) {
            if(id == R.id.action_list_popular) {
                mCurrentList = Utility.POPULAR;
            } else if(id == R.id.action_list_rating) {
                mCurrentList = Utility.RATING;
            } else{
                mCurrentList = Utility.FAVORITE;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            prefs.edit().putInt(CURRENT_LIST_KEY, mCurrentList).apply();
            //List is refreshed so reset the saved position
            mItemPosition=-1;
            //reload the data
            onListChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(LOG_TAG, "onSaveInstanceState");

        if(GridView.INVALID_POSITION != mItemPosition) {
            outState.putInt(ITEM_POSITION_KEY, mItemPosition);
        }
        outState.putInt(CURRENT_LIST_KEY, mCurrentList);
        super.onSaveInstanceState(outState);
    }

    /**
     * Updates the GridView by calling retrofit function
     */
    private void updateMovieList() {

        //Do not fetch data if the list showing is favorites. It's saved in database
        if (mCurrentList !=  Utility.FAVORITE){
            String sortOrder;
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            if (mCurrentList == Utility.RATING) {
                sortOrder = TMDB.SORT_RATING;
            } else {
                sortOrder = TMDB.SORT_POPULAR;
            }

            Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
            getActivity().getContentResolver().delete(movieUri, null, null);

            RestClient.getTMDBApiClient().getMovies(sortOrder, TMDB.MIN_VOTE, TMDB.API_KEY, new Callback<List<MovieItem>>() {
                @Override
                public void success(List<MovieItem> movieItems, Response response) {
                    int size = movieItems.size();
                    if (size > 0) {

                        Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                        for (MovieItem m : movieItems) {
                            ContentValues movieValues = new ContentValues();

                            movieValues.put(MovieContract.MovieEntry._ID, m.getId());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, m.getTitle());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, m.getPosterUrl("medium"));
                            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_URL, m.getBackdropUrl("medium"));
                            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, m.getSynopsis());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_GENRE, Utility.implodeInt(m.getGenre()));
                            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, m.getReleaseDate().getTime());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, m.getPopularity());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVG, m.getUserRating());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, m.getVoteCount());
                            movieValues.put(MovieContract.MovieEntry.COLUMN_DATE_ADDED, MovieContract.normalizeDate(System.currentTimeMillis()));
                            cVVector.add(movieValues);


                        }
                        int inserted = 0;
                        // add to database
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                        }
                        if (DEBUG) Log.i(LOG_TAG, "updateMovieList Complete. " + inserted + " Inserted");

                    }

                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);


                }

                @Override
                public void failure(RetrofitError error) {
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Error : " + error.getMessage());
                }
            });
        }
    }

    /**
     * Fetch and store movie genres in database
     */
    private void updateGenreList() {


        //Fetch genre list from TMDB server
        RestClient.getTMDBApiClient().getGenre(TMDB.API_KEY, new Callback<List<GenreItem>>() {
            @Override
            public void success(List<GenreItem> genreItems, Response response) {
                int size = genreItems.size();
                if (size > 0) {

                    Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                    for (GenreItem m : genreItems) {
                        ContentValues movieValues = new ContentValues();

                        movieValues.put(MovieContract.GenreEntry._ID, m.getId());
                        movieValues.put(MovieContract.GenreEntry.COLUMN_NAME, m.getName());
                        cVVector.add(movieValues);


                    }
                    int inserted = 0;
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        inserted = getActivity().getContentResolver().bulkInsert(MovieContract.GenreEntry.CONTENT_URI, cvArray);
                    }
                    if (DEBUG)
                        Log.d(LOG_TAG, "updateGenreList Complete. " + inserted + " Inserted");

                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Error : " + error.getMessage());
            }
        });
    }

    /**
     * When the user changes the list from options menu. reload the data and restart the cursor loader
     */

    private void onListChanged() {
        updateMovieList();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    /**
     * Interface implemented by MainActivity to launch movie details fragment or activity
     */
    public interface ClickCallback {
        void onItemSelected(Uri movieUri);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sort_order;
        Uri uri;
        if(Utility.FAVORITE == mCurrentList){
            //Show latest added favorites first
            sort_order = MovieContract.FavoriteEntry.COLUMN_DATE_ADDED + " DESC";
            uri = MovieContract.FavoriteEntry.CONTENT_URI;

        } else{
            uri = MovieContract.MovieEntry.CONTENT_URI;
            if(Utility.RATING == mCurrentList) {
                sort_order = MovieContract.MovieEntry.COLUMN_VOTE_AVG + " DESC, " + MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " DESC";
            } else{
                sort_order=MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            }

        }
        return new CursorLoader(getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                sort_order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor != null) {
            if (mCurrentList == Utility.FAVORITE) {
                if (!cursor.moveToFirst()) {
                    Toast.makeText(getActivity(), getString(R.string.favorite_empty), Toast.LENGTH_SHORT).show();
                }
            }
            mMovieAdapter.swapCursor(cursor);


            //Show first item in list if nothing is selected so that fragment is not empty in two pane layout
            if(((MainActivity) this.getActivity()).isTwoPane()){
                if (mGridView.getSelectedItemPosition() == GridView.INVALID_POSITION) {
                    mGridView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int itemPosition = 0;
                            if (mItemPosition != GridView.INVALID_POSITION) {
                                itemPosition = mItemPosition;
                            }
                            int tot = mGridView.getCount();
                            if (tot > 0 && tot >= itemPosition) {
                                mGridView.setItemChecked(itemPosition, true);
                                mGridView.performItemClick(mGridView.getChildAt(itemPosition), itemPosition, itemPosition);
                                mGridView.smoothScrollToPosition(itemPosition);
                                //so that if mItemPosition is invalid it is set to 0
                                mItemPosition = itemPosition;
                            }
                        }
                    }, 1000);
                }
            }




            if (mItemPosition != GridView.INVALID_POSITION) {
                    mGridView.smoothScrollToPosition(mItemPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }


//FOLLOWING FUNCTIONS FOR DEBUGGING PURPOSE ONLY.

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.i(LOG_TAG, "onStart()");
    }



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
