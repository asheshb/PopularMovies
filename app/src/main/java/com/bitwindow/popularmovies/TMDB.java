package com.bitwindow.popularmovies;

/**
 * Class to handle all TMDB related acitvities
 */
public class TMDB {
    private static final String LOG_TAG = TMDB.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    private final static String IMG_BASE_URL ="http://image.tmdb.org/t/p/";
    //PLEASE INPUT YOUR TMDB KEY HERE
    public final static String API_KEY = "";
    public final static String BASE_URL = "https://api.themoviedb.org/3/";
    public final static String ID_PARAM = "id";
    public final static String API_KEY_PARAM = "api_key";
    public final static String SORT_PARAM = "sort_by";
    public final static String MIN_VOTE_PARAM = "vote_count.gte";
    //To filter out those movies which got very few votes and high rating so that a movie with 10 rating and 1 vote count don't sit at the top
    public final static String MIN_VOTE="100";
    public final static String SORT_RATING="vote_average.desc";
    public final static String SORT_POPULAR="popularity.desc";

    /*
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

}
