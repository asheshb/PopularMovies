package com.bitwindow.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by ashbey on 8/26/2015.
 * This class defines various tables and its columns in database
 *
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.bitwindow.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_MOVIE = "movie";
    private static final String PATH_REVIEW = "review";
    private static final String PATH_VIDEO = "video";
    private static final String PATH_GENRE = "genre";
    private static final String PATH_FAVORITE = "favorite";



    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_GENRE = "genre_ids";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DATE_ADDED = "date_added";



        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the favorite table */
    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_GENRE = "genre_ids";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVG = "vote_avg";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DATE_ADDED = "date_added";



        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    /* Inner class that defines the table contents of the review table */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        //SO that reviews are shown in same order as TMDB
        public static final String COLUMN_POSITION = "position";



        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the review table */
    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_KEY = "key";




        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the review table */
    public static final class GenreEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        public static final String TABLE_NAME = "genre";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";




        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
