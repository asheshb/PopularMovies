package com.bitwindow.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bitwindow.popularmovies.data.MovieContract.MovieEntry;
import com.bitwindow.popularmovies.data.MovieContract.ReviewEntry;
import com.bitwindow.popularmovies.data.MovieContract.VideoEntry;

/**
 * Created by ashbey on 8/26/2015.
 * This class creates the database and its tables in SQLite
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 19;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_GENRE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_DATE_ADDED + " INTEGER NOT NULL" +
                ");";

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_BACKDROP_URL + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_GENRE + " TEXT NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " INTEGER NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                MovieContract.FavoriteEntry.COLUMN_DATE_ADDED + " INTEGER NOT NULL" +
                ");";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +

                // Set up the movie id column as a foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +


                ");";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_KEY + " TEXT NOT NULL, " +

                // Set up the movie id column as a foreign key to movie table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +


                ");";

        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + MovieContract.GenreEntry.TABLE_NAME + " (" +
                MovieContract.GenreEntry._ID + " INTEGER PRIMARY KEY," +
                VideoEntry.COLUMN_NAME + " TEXT NOT NULL " +

                ");";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.GenreEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
