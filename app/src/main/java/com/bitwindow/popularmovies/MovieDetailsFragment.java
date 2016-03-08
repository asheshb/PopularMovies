package com.bitwindow.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bitwindow.popularmovies.adapter.ReviewListAdapter;
import com.bitwindow.popularmovies.adapter.VideoListAdapter;
import com.bitwindow.popularmovies.api.RestClient;
import com.bitwindow.popularmovies.data.MovieContract;
import com.bitwindow.popularmovies.data.MovieProvider;
import com.bitwindow.popularmovies.pojo.ReviewItem;
import com.bitwindow.popularmovies.pojo.VideoItem;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Fragment to show movie details when user clicks on a grid view item
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, VideoListAdapter.VideoListAdapterCallback {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int VIDEO_LOADER = 2;


    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_BACKDROP_URL,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_GENRE,
    };
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_BACKDROP_URL = 1;
    private static final int COL_TITLE = 2;
    private static final int COL_POSTER_URL = 3;
    private static final int COL_RELEASE_DATE = 4;
    private static final int COL_VOTE_AVG = 5;
    private static final int COL_VOTE_COUNT = 6;
    private static final int COL_SYNOPSIS = 7;
    private static final int COL_GENRE = 8;


    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
    };
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;


    private static final String[] VIDEO_COLUMNS = {
            MovieContract.VideoEntry.TABLE_NAME + "." + MovieContract.VideoEntry._ID,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_KEY,
    };
    public static final int COL_VIDEO_NAME = 1;
    public static final int COL_VIDEO_KEY = 2;


    private ReviewListAdapter mReviewAdapter;

    private VideoListAdapter mVideoListAdapter;

    // If the movie is favorite
    private int mFavorite =0;
    // The uri passed to this fragment
    private Uri mItemUri = null;
    // The movie id passed to this fragment
    private Long mMovieId;



    @Bind(R.id.ivBackDrop) ImageView mivBackDrop;
    @Bind(R.id.tvTitle) TextView mtvTitle;
    @Bind(R.id.ivPoster) ImageView mivPoster;
    @Bind(R.id.tvRating) TextView mtvRating;
    @Bind(R.id.tvSynopsis) TextView mtvSynopsis;
    @Bind(R.id.tvGenre)  TextView mtvGenre;
    @Bind(R.id.tvReleaseDate) TextView mtvReleaseDate;
    @Bind(R.id.tvSynopsisHeader) TextView mtvSummaryHeader;
    @Bind(R.id.tvVideoHeader) TextView mtvVideoHeader;
    @Bind(R.id.tvReviewHeader) TextView mtvReviewHeader;
    @Bind(R.id.ibtnFavorite) ImageButton mibtnFavorite;

    // Video loader progress bar
    private ProgressBar mpbVideo;

    // To share video url
    private ShareActionProvider mShareActionProvider;
    private String mVideoShareText;
    private static final String mYouTubeUrl = "https://www.youtube.com/watch?v=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateView()");

        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemUri = arguments.getParcelable(DETAIL_URI);
            if(mItemUri != null) {
                mMovieId = Long.parseLong(mItemUri.getLastPathSegment());
                //If the current list showing is favorite then no need to query the database
                if (MovieProvider.FAVORITE_ID == MovieProvider.URI_MATCHER.match(mItemUri)) {
                    mFavorite = 1;
                } else if (checkRecordExists(MovieContract.FavoriteEntry.CONTENT_URI, MovieContract.FavoriteEntry._ID, Long.toString(mMovieId))) {
                    mFavorite = 1;
                }
            }

        }

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this,rootView);

        mReviewAdapter = new ReviewListAdapter(getActivity(), null, 0);

        //Custom list view without scroll bar wrapped to the height of reviews
        WrappedListView wrappedListView = (WrappedListView)   rootView.findViewById(R.id.lvReview);
        wrappedListView.setAdapter(mReviewAdapter);
        wrappedListView.setExpanded(true);

        ((WrappedListView) rootView.findViewById(R.id.lvReview)).setAdapter(mReviewAdapter);

        mVideoListAdapter = new VideoListAdapter(getActivity(), null, 0, this);

        //Custom grid view without scroll bar wrapped to the height of videos
        WrappedGridView wrappedGridView = (WrappedGridView) rootView.findViewById(R.id.gvVideo);
        wrappedGridView.setAdapter(mVideoListAdapter);
        wrappedGridView.setExpanded(true);


        mpbVideo = ((ProgressBar) rootView.findViewById(R.id.pbVideo));


        mtvSummaryHeader.setText(getString(R.string.summary));
        mtvVideoHeader.setText(getString(R.string.videos));
        mtvReviewHeader.setText(getString(R.string.reviews));

        mibtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(mFavorite ==0){
                    mFavorite=1;
                    ContentValues values = new ContentValues();
                    values.put("id", mMovieId);
                    getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);

                    Toast.makeText(getActivity(), getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                } else{
                    mFavorite=0;
                    getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.buildFavoriteUri(mMovieId), null, null);
                    Toast.makeText(getActivity(), getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                }
                toggleFavoriteButton();
            }
        });

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateOptionsMenu()");

        inflater.inflate(R.menu.menu_movie_details_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);


            // Get the provider and hold onto it to set/change the share intent.
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);


        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mVideoShareText != null) {
            mShareActionProvider.setShareIntent(createShareVideoIntent());
        }

    }


    /**
     * Called from VideoListAdapter when a video is clicked by user
     * @param key the key to youtube video
     */
    @Override
    public void onClickCallback(String key) {
        if (Utility.isAppInstalled("com.google.android.youtube", getActivity())) {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                startActivity(intent);
            }
        } else {
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                startActivity(intent);

            } catch (ActivityNotFoundException ex) {
                Toast.makeText(getActivity(),getString(R.string.video_error),Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onActivityCreated()");
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
        if(savedInstanceState == null) {
            updateReviewList();
            updateVideoList();
        }
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(DEBUG) Log.i(LOG_TAG, "In onCreateLoader");
        if (null != mItemUri) {
            if (DETAIL_LOADER == i) {


                return new CursorLoader(getActivity(),
                        mItemUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);

            } else if (REVIEW_LOADER == i) {
                    return new CursorLoader(getActivity(),
                            MovieContract.ReviewEntry.CONTENT_URI,
                            REVIEW_COLUMNS,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                            new String[]{Long.toString(mMovieId)},
                            MovieContract.ReviewEntry.COLUMN_POSITION + " DESC");
            } else if (VIDEO_LOADER == i) {
                return new CursorLoader(getActivity(),
                        MovieContract.VideoEntry.CONTENT_URI,
                        VIDEO_COLUMNS,
                        MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(mMovieId)},
                        null);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(DEBUG) Log.i(LOG_TAG, "In onLoadFinished");
        if (cursor == null || !cursor.moveToFirst()) { return; }

        if(DETAIL_LOADER == cursorLoader.getId()) {
            toggleFavoriteButton();
            String title = cursor.getString(COL_TITLE);

            mivBackDrop.setContentDescription(title);
            Picasso.with(getActivity()).load(TMDB.getImageBaseUrl("large") + cursor.getString(COL_BACKDROP_URL)).into(mivBackDrop);

            //mtvTitle.setText(cursor.getString(COL_MOVIE_ID));
            mtvTitle.setText(title);
            mtvTitle.setBackgroundColor(0x26444444);

            Picasso.with(getActivity()).load(TMDB.getImageBaseUrl("small") + cursor.getString(COL_POSTER_URL)).into(mivPoster);

            mtvRating.setText(getResources().getQuantityString(R.plurals.rating_desc, cursor.getInt(COL_VOTE_COUNT), cursor.getFloat(COL_VOTE_AVG), cursor.getInt(COL_VOTE_COUNT)));
            mtvSynopsis.setText(cursor.getString(COL_SYNOPSIS));

            //Get names from genre table for the ids fetched from movie/favorite table
            String genre = cursor.getString(COL_GENRE);
            if(genre.length()>0) {
                String[] genreList = genre.split(",");
                String sep = ", ";
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < genreList.length; i++) {
                    sb.append("?");
                    if (i != genreList.length - 1) {
                        sb.append(sep);
                    }
                }
                Cursor genreCursor = getActivity().getContentResolver().query(MovieContract.GenreEntry.CONTENT_URI, new String[]{MovieContract.GenreEntry.COLUMN_NAME}, MovieContract.GenreEntry._ID + " IN ( " + sb.toString() + " )", genreList, null);
                try {
                    sb = new StringBuilder();
                    int tot = genreCursor.getCount();
                    int i = 0;
                    while (genreCursor.moveToNext()) {
                        sb.append(genreCursor.getString(0));
                        if (i != tot - 1) {
                            sb.append(sep);
                        }
                        i++;
                    }
                    mtvGenre.setText(sb.toString());
                } finally {
                    if (genreCursor != null) {
                        genreCursor.close();
                    }
                }
            }



            try {
                SimpleDateFormat df = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
                mtvReleaseDate.setText(df.format(cursor.getLong(COL_RELEASE_DATE)));
            } catch (Exception e) {
                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if(mtvSummaryHeader.getVisibility() == View.INVISIBLE) mtvSummaryHeader.setVisibility(View.VISIBLE);
        } else if(REVIEW_LOADER == cursorLoader.getId()){
            mReviewAdapter.swapCursor(cursor);
            if(mtvReviewHeader.getVisibility() == View.INVISIBLE) mtvReviewHeader.setVisibility(View.VISIBLE);
        } else if(VIDEO_LOADER == cursorLoader.getId()){
            mVideoListAdapter.swapCursor(cursor);
            if(mtvVideoHeader.getVisibility() == View.INVISIBLE) mtvVideoHeader.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        if(REVIEW_LOADER == cursorLoader.getId()){
            mReviewAdapter.swapCursor(null);
        } else if(VIDEO_LOADER == cursorLoader.getId()){
            mVideoListAdapter.swapCursor(null);
        }
    }


    private void updateReviewList() {

        if (null == mItemUri) {
            return;
        }
        //If the reviews are already in db then no ned to fetch them from TMDB server
        if(checkRecordExists(MovieContract.ReviewEntry.CONTENT_URI, MovieContract.ReviewEntry.COLUMN_MOVIE_ID, Long.toString(mMovieId))) {
            Log.i(LOG_TAG,"Update review in DB " + mMovieId.toString());
            return;

        }

        //Fetch reviews from TMDB server and store in database
        RestClient.getTMDBApiClient().getReviews(Long.toString(mMovieId), BuildConfig.TMDB_API_KEY, new Callback<List<ReviewItem>>() {
            @Override
            public void success(List<ReviewItem> reviewItems, Response response) {
                int size = reviewItems.size();
                if (size > 0) {
                    int counter = 0;
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                    for (ReviewItem m : reviewItems) {
                        ContentValues movieValues = new ContentValues();

                        movieValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, mMovieId);
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, m.getAuthor());
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, m.getContent());
                        movieValues.put(MovieContract.ReviewEntry.COLUMN_POSITION, counter);
                        cVVector.add(movieValues);
                        counter++;
                    }
                    int inserted = 0;
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        FragmentActivity fa = getActivity();
                        if (fa != null) {
                            inserted = fa.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
                        }
                    }
                    if (DEBUG)
                        Log.i(LOG_TAG, "updateReviewList Complete. " + inserted + " Inserted");

                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Error : " + error.getMessage());
            }
        });

    }



    private void updateVideoList() {

        if (null == mItemUri) {
            return;
        }
        //If the videos are already in db then no ned to fetch them from TMDB server
        if(checkRecordExists(MovieContract.VideoEntry.CONTENT_URI, MovieContract.VideoEntry.COLUMN_MOVIE_ID, Long.toString(mMovieId))) {
            mpbVideo.setVisibility(ProgressBar.GONE);

            //Set the share intent url if in database
            getVideoUrl();
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareVideoIntent());
            }
            return;
        }


        mpbVideo.setVisibility(ProgressBar.VISIBLE);

        //Fetch videos from TMDB server and store in database
        RestClient.getTMDBApiClient().getVideos(Long.toString(mMovieId), BuildConfig.TMDB_API_KEY, new Callback<List<VideoItem>>() {
            @Override
            public void success(List<VideoItem> videoItems, Response response) {
                int size = videoItems.size();
                if (size > 0) {
                    int counter = 0;
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(size);
                    for (VideoItem m : videoItems) {
                        ContentValues movieValues = new ContentValues();
                        if (m.getSite().equals("YouTube")) {
                            if(0 == counter) {
                                //Set the share intent url
                                mVideoShareText = m.getName() + " - " +  mYouTubeUrl + m.getKey();
                                if (mShareActionProvider != null) {
                                    mShareActionProvider.setShareIntent(createShareVideoIntent());
                                }
                            }

                            movieValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, mMovieId);
                            movieValues.put(MovieContract.VideoEntry.COLUMN_NAME, m.getName());
                            movieValues.put(MovieContract.VideoEntry.COLUMN_KEY, m.getKey());
                            cVVector.add(movieValues);
                            counter++;
                        }
                    }
                    int inserted = 0;
                    // add to database
                    if (counter > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        FragmentActivity fa = getActivity();
                        if (fa != null) {
                            inserted = getActivity().getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
                        }
                    }
                    if (DEBUG)
                        Log.i(LOG_TAG, "updateVideoList Complete. " + inserted + " Inserted");

                }

                mpbVideo.setVisibility(ProgressBar.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, "Error : " + error.getMessage());
                mpbVideo.setVisibility(ProgressBar.GONE);
            }
        });

    }

    /**
     * To create a share intent for movide video
     * @return
     */
    private Intent createShareVideoIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mVideoShareText);
        return shareIntent;
    }


    /**
     * To toggle the favorite button image when user add or remove favorite
     */
    private void toggleFavoriteButton(){
        if(mFavorite ==1){
            mibtnFavorite.setImageResource(R.drawable.favorite_on);
            mibtnFavorite.setContentDescription(getString(R.string.favorite_remove));
        } else{
            mibtnFavorite.setImageResource(R.drawable.favorite_off);
            mibtnFavorite.setContentDescription(getString(R.string.favorite_add));
        }
    }

    /**
     * Check if data is already in database
     * @param uri the uri to check
     * @param col_name name of the column
     * @param col_value value of the column
     * @return true if record exists
     */
    private boolean checkRecordExists(Uri uri, String col_name, String col_value){
        boolean exists = false;
        Cursor cursor = getActivity().getContentResolver().query(uri,new String[] {"COUNT(*)"},  col_name + " = ?", new String[]{col_value},null);
        try {
            if (cursor.moveToFirst()) {
                if (cursor.getInt(0) > 0) {
                    cursor.close();
                    exists=true;
                }
            }
        } finally{
            cursor.close();
        }
        return exists;
    }


    /**
     *  Get the video url from database for share intent
     */
    private void getVideoUrl(){
        Uri uri = MovieContract.VideoEntry.CONTENT_URI.buildUpon()
                .appendQueryParameter(MovieProvider.QUERY_PARAMETER_LIMIT,
                        "1").build();
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{MovieContract.VideoEntry.COLUMN_NAME, MovieContract.VideoEntry.COLUMN_KEY}, MovieContract.VideoEntry.COLUMN_MOVIE_ID  + " = ?", new String[]{Long.toString(mMovieId)}, null);
        try {
            if (cursor.moveToFirst()) {
                mVideoShareText = cursor.getString(0) + " - "+  mYouTubeUrl + cursor.getString(1);
            }
        } finally{
            cursor.close();
        }

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
