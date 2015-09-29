package com.bitwindow.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by ashbey on 9/1/2015.
 * This class is used by app to interact with database
 */
public class MovieProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;

    // helper constants for use with the UriMatcher
    private static final int MOVIE_LIST = 1;
    private static final int MOVIE_ID = 2;
    private static final int REVIEW_LIST = 3;
    private static final int VIDEO_LIST = 4;
    private static final int GENRE_LIST = 5;
    private static final int FAVORITE_LIST = 6;
    public static final int FAVORITE_ID = 7;
    public static final UriMatcher URI_MATCHER;


    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "movie",
                MOVIE_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "movie/#",
                MOVIE_ID);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "review",
                REVIEW_LIST);

        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "video",
                VIDEO_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "genre",
                GENRE_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "favorite",
                FAVORITE_LIST);
        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                "favorite/#",
                FAVORITE_ID);

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW_LIST:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case VIDEO_LIST:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case GENRE_LIST:
                return MovieContract.GenreEntry.CONTENT_TYPE;
            case FAVORITE_LIST:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_LIST: {
                normalizeDate(values);
                long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if ( _id > 0 ) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_LIST: {
                //Instead of fetching the data from movie table in app and copying it to favorite table. Directly insert the data using movie id
                String id = values.getAsString("id");
                String sql = "INSERT INTO " + MovieContract.FavoriteEntry.TABLE_NAME + " SELECT * FROM " + MovieContract.MovieEntry.TABLE_NAME + " WHERE " + MovieContract.MovieEntry._ID + " = " + id;
                db.execSQL(sql);
                //Update date_added so that latest favorites added are shown at the top
                sql = "UPDATE " + MovieContract.FavoriteEntry.TABLE_NAME + " SET " + MovieContract.FavoriteEntry.COLUMN_DATE_ADDED + " = " +  Long.toString(System.currentTimeMillis()) + " WHERE " + MovieContract.FavoriteEntry._ID + " = " + id;
                db.execSQL(sql);
                returnUri= MovieContract.FavoriteEntry.buildFavoriteUri(Long.parseLong(id));
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEO_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case GENRE_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insertWithOnConflict(MovieContract.GenreEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_IGNORE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (URI_MATCHER.match(uri)) {
            // "movie/#"
            case MOVIE_ID: {
                builder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                builder.appendWhere(MovieContract.MovieEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            }
            // "movie"
            case MOVIE_LIST: {
                builder.setTables(MovieContract.MovieEntry.TABLE_NAME);
                break;
            }
            // "review/#"
            case REVIEW_LIST: {
                builder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
                break;
            }
            // "video/#"
            case VIDEO_LIST: {
                builder.setTables(MovieContract.VideoEntry.TABLE_NAME);
                break;
            }
            // "genre"
            case GENRE_LIST: {
                builder.setTables(MovieContract.GenreEntry.TABLE_NAME);
                break;
            }
            // "favorite/#"
            case FAVORITE_ID: {
                builder.setTables(MovieContract.FavoriteEntry.TABLE_NAME);
                builder.appendWhere(MovieContract.FavoriteEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            }
            // "favorite"
            case FAVORITE_LIST: {
                builder.setTables(MovieContract.FavoriteEntry.TABLE_NAME);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Cursor cursor =
                builder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updateCount;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                updateCount = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_ID:
                String idStr = uri.getLastPathSegment();
                String where = MovieContract.MovieEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int delCount = 0;
        String idStr;
        String where;
        switch (URI_MATCHER.match(uri)) {
            case MOVIE_LIST:
                delCount = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case MOVIE_ID:
                idStr = uri.getLastPathSegment();
                where = MovieContract.MovieEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            case REVIEW_LIST:
                delCount = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case VIDEO_LIST:
                delCount = db.delete(
                        MovieContract.VideoEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case GENRE_LIST:
                delCount = db.delete(
                        MovieContract.GenreEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case FAVORITE_LIST:
                delCount = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case FAVORITE_ID:
                idStr = uri.getLastPathSegment();
                where = MovieContract.FavoriteEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            default:
                // no support for deleting photos or entities –
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)) {
            long dateValue = values.getAsLong(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.normalizeDate(dateValue));
        }
    }
}
