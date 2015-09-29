package com.bitwindow.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitwindow.popularmovies.MovieDetailsFragment;
import com.bitwindow.popularmovies.R;

/**
 * Created by ashbey on 9/5/2015.
 * This class is used by MovieDetailsFragment > ListView to show movie reviews from database
 */
public class ReviewListAdapter extends CursorAdapter {
    public ReviewListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
        viewHolder.tvContent = (TextView) view.findViewById(R.id.tvContent);
        view.setTag(viewHolder);
        return view;
    }


    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.tvAuthor.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_AUTHOR));
        viewHolder.tvContent.setText(cursor.getString(MovieDetailsFragment.COL_REVIEW_CONTENT));
    }


    class ViewHolder {
        TextView tvAuthor;
        TextView tvContent;
    }
}
