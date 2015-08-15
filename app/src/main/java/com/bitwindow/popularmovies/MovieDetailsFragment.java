package com.bitwindow.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;


/**
 * Fragment to show movie details when user clicks on a grid view item
 */
public class MovieDetailsFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final boolean DEBUG = false; // Set this to false to disable logs.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) Log.i(LOG_TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Bundle data = getActivity().getIntent().getExtras();
        MovieItem movieItem = data.getParcelable(getString(R.string.movie_item_key));

        HashMap<Integer,String> genres = (HashMap<Integer,String>)  data.getSerializable(getString(R.string.genres_key));

        String title = movieItem.getTitle();
        String url = movieItem.getBackdropUrl("large");
        ImageView ivPoster = (ImageView) rootView.findViewById(R.id.ivBackDrop);
        ivPoster.setContentDescription(title);
        Picasso.with(getActivity()).load(url).into(ivPoster);


        url = movieItem.getPosterUrl("medium");
        ivPoster = (ImageView) rootView.findViewById(R.id.ivPoster);
        ivPoster.setContentDescription(title);
        Picasso.with(getActivity()).load(url).into(ivPoster);

        TextView tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        tvTitle.setBackgroundColor(0x26444444);

        TextView tvRating = (TextView) rootView.findViewById(R.id.tvRating);
        tvRating.setText(getResources().getQuantityString(R.plurals.rating_desc, movieItem.getVoteCount(), movieItem.getUserRating(), movieItem.getVoteCount()));

        TextView tvSynopsis = (TextView) rootView.findViewById(R.id.tvSynopsis);
        tvSynopsis.setText(movieItem.getSynopsis());


        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        tvReleaseDate.setText(movieItem.getReleaseDate());


        if(genres!= null) {
            int[] movie_genres = movieItem.getGenres();
            if(movie_genres != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < movie_genres.length; i++) {
                    if (genres.containsKey(movie_genres[i])) {
                        if(sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(genres.get(movie_genres[i]));
                    }
                }
                TextView tvGenres = (TextView) rootView.findViewById(R.id.tvGenres);
                tvGenres.setText(sb.toString());
            }
        }

        return rootView;
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