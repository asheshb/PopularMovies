package com.bitwindow.popularmovies;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Class to handle all TMDB related acitvities
 */
class TMDB {
    private static final String LOG_TAG = TMDB.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private final static String IMG_BASE_URL ="http://image.tmdb.org/t/p/";
    //PLEASE INPUT YOUR TMDB KEY HERE
    private final static String API_KEY = "";
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private final static String API_KEY_PARAM = "api_key";
    private final static String SORT_PARAM = "sort_by";
    //To filter out those movies which got very few votes and high rating so that a movie with 10 rating and 1 vote count don't sit at the top
    private final static String MIN_VOTE="vote_count.gte";

    /**
     * Returns param used in TMDB URL to fetch poster size.
     * @param size param for the poster size used in TMDB poster URL
     * @return the param to be used based on readable size formats
     */
    public static String getImageBaseUrl(String size) {
        switch (size) {
            case "small":
                return IMG_BASE_URL + "w154";
            case "large":
                return IMG_BASE_URL + "w500";
            default:
                return IMG_BASE_URL + "w185";
        }

    }

    /**
     * Returns the url to fetch movies data
     * @param sort_order to set the sort order,  most popular or highest rated
     * @return URL of the movies on TMDB database
     */
    public static URL getMovieUrl(String sort_order){
        String sort_by = "popularity.desc";
        //To filter out 1 vote / 5 star wonders
        String min_vote = "200";
        if (sort_order.equals("rating")) {
            sort_by = "vote_average.desc";
        }
        try{
            Uri builtUri = Uri.parse(TMDB_BASE_URL + "discover/movie?").buildUpon()
                    .appendQueryParameter(SORT_PARAM, sort_by)
                    .appendQueryParameter(MIN_VOTE, min_vote)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY).build();

            return new URL(builtUri.toString());

        } catch(Exception e){
            //TODO: How to handle this? As such there is not a chance of error since the url is inbuilt
        }
        return null;
    }


    /**
     * Returns the url to fetch Genres list
     * @return URL of the genre list on TMDB database
     */
    public static URL getGenresUrl(){
        String min_vote = "1000";
        try{
            Uri builtUri = Uri.parse(TMDB_BASE_URL + "genre/movie/list?").buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, API_KEY).build();

            return new URL(builtUri.toString());

        } catch(Exception e){
            //TODO: How to handle this? As such there is not a chance of error since the url is inbuilt
        }
        return null;
    }


    /**
     * Resturns the array list of movie items parsed from the json string returned by server
     * @param jsonStr the json string containing movies fetched from server
     * @return Arraylist of MovieItem class objects
     * @throws JSONException
     */
    public static ArrayList<MovieItem> getMovieFromJson(String jsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TMD_RESULTS = "results";
        final String TMD_ID = "id";
        final String TMD_BACKDROP = "backdrop_path";
        final String TMD_TITLE = "original_title";
        final String TMD_USER_RATING = "vote_average";
        final String TMD_SYNOPSIS = "overview";
        final String TMD_RELEASE_DATE = "release_date";
        final String TMD_POSTER = "poster_path";
        final String TMD_VOTE_COUNT = "vote_count";
        final String TMD_GENRES = "genre_ids";

        ArrayList<MovieItem> movieItems = new ArrayList<MovieItem>();
        JSONObject dataJson = new JSONObject(jsonStr);
        JSONArray pageArray = dataJson.getJSONArray(TMD_RESULTS);
        for(int i = 0; i < pageArray.length(); i++) {
            MovieItem movieItem = new MovieItem();
            dataJson = pageArray.getJSONObject(i);

            movieItem.setId(dataJson.getInt(TMD_ID));
            movieItem.setBackdropUrl(dataJson.getString(TMD_BACKDROP));
            movieItem.setTitle(dataJson.getString(TMD_TITLE));
            movieItem.setUserRating(dataJson.getDouble(TMD_USER_RATING));
            movieItem.setSynopsis(dataJson.getString(TMD_SYNOPSIS));
            String dateStr = dataJson.getString(TMD_RELEASE_DATE);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy", Locale.getDefault());
                String releaseDate = formatter.format(  new SimpleDateFormat("yyyy-MM-dd").parse(dateStr));
                movieItem.setReleaseDate(releaseDate);
            } catch(ParseException e){

            }
            movieItem.setPosterUrl(dataJson.getString(TMD_POSTER));
            movieItem.setVoteCount(dataJson.getInt(TMD_VOTE_COUNT));
            JSONArray genreArray = dataJson.getJSONArray(TMD_GENRES);
            if(genreArray != null) {
                int[] genres = new int[genreArray.length()];
                for (int j = 0; j < genreArray.length(); j++) {
                    genres[j] = genreArray.getInt(j);
                }
                movieItem.setGenres(genres);
            }

            movieItems.add(movieItem);
        }

        return movieItems;
    }

    /**
     * Resturns the hashmap of genre parsed from the json string returned by server
     * @param jsonStr the json string containing movies fetched from server
     * @return hashmap of Genre
     * @throws JSONException
     */
    public static HashMap<Integer, String> getGenresFromJson(String jsonStr)
            throws JSONException {
        HashMap<Integer, String> genres = new HashMap<Integer, String>();

        final String TMD_ROOT = "genres";
        final String TMD_ID = "id";
        final String TMD_NAME = "name";

        JSONObject dataJson = new JSONObject(jsonStr);
        JSONArray pageArray = dataJson.getJSONArray(TMD_ROOT);
        for(int i = 0; i < pageArray.length(); i++) {
            dataJson = pageArray.getJSONObject(i);
            genres.put(dataJson.getInt(TMD_ID),dataJson.getString(TMD_NAME));
        }
        return genres;
    }

}
