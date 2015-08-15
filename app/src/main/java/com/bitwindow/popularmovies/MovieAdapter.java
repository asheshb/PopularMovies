package com.bitwindow.popularmovies;

/**
 * Created by ashbey on 8/14/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom adapter class to feed the data to GridView
 */
public class MovieAdapter extends BaseAdapter {
    private final Context mContext;
    ArrayList<MovieItem> mlist = new ArrayList<MovieItem>();

    public MovieAdapter(Context c, ArrayList<MovieItem> list) {
        mContext = c;
        mlist = list;
    }

    public int getCount() {
        return mlist.size() ;
    }

    public MovieItem getItem(int position) {
        return mlist.get(position);
    }

    public long getItemId(int position) {
        return mlist.get(position).getId() ;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_grid_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            LayerDrawable stars = (LayerDrawable) viewHolder.ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP); // for filled stars
            stars.getDrawable(1).setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_ATOP); // for half filled stars
            stars.getDrawable(0).setColorFilter(Color.GRAY,PorterDuff.Mode.SRC_ATOP); // for empty stars
            viewHolder.poster = (ImageView) convertView.findViewById(R.id.poster);
            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MovieItem movieItem = mlist.get(position);
        String url = movieItem.getPosterUrl("medium");
        Picasso.with(mContext).load(url).into(viewHolder.poster);
        viewHolder.poster.setContentDescription(movieItem.getTitle());
        viewHolder.ratingBar.setRating(movieItem.getUserRating().floatValue());
        return convertView;
    }

    public void updateAdapter(ArrayList<MovieItem> list) {
        mlist= list;
        this.notifyDataSetChanged();
    }

    class ViewHolder {
        RatingBar ratingBar;
        ImageView poster;
    }

}
