package com.bitwindow.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bitwindow.popularmovies.MainActivityFragment;
import com.bitwindow.popularmovies.R;
import com.bitwindow.popularmovies.TMDB;
import com.squareup.picasso.Picasso;

/**
 * Created by ashbey on 9/5/2015.
 * This class is used by MainActivityFragment > GridView to show movie posters from database
 */
public class MovieListAdapter extends CursorAdapter {
    public MovieListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_grid_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) viewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_ATOP); // for empty stars
        viewHolder.poster = (ImageView) view.findViewById(R.id.poster);
        viewHolder.tvMovieTitle = (TextView) view.findViewById(R.id.tvMovieTitle);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        ImageView imageView = viewHolder.poster;
        String url = TMDB.getImageBaseUrl("medium") + cursor.getString(MainActivityFragment.COL_POSTER_URL);
        Picasso.with(context).load(url).into(imageView);
        viewHolder.poster.setContentDescription(cursor.getString(MainActivityFragment.COL_TITLE));
        viewHolder.ratingBar.setRating(cursor.getFloat(MainActivityFragment.COL_VOTE_AVG));
        viewHolder.tvMovieTitle.setText(cursor.getString(MainActivityFragment.COL_TITLE));
    }


    class ViewHolder {
        RatingBar ratingBar;
        ImageView poster;
        TextView tvMovieTitle;
    }
}
