package com.bitwindow.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitwindow.popularmovies.MovieDetailsFragment;
import com.bitwindow.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by ashbey on 9/5/2015.
 * This class is used by MovieDetailsFragment > GridView to show movie videos from database
 */
public class VideoListAdapter extends CursorAdapter {
    private final VideoListAdapterCallback mVideoListAdapterCallback;

    public VideoListAdapter(Context context, Cursor c, int flags, VideoListAdapterCallback videoListAdapterCallback) {
        super(context, c, flags);
        this.mVideoListAdapterCallback = videoListAdapterCallback;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.ivVideo = (ImageView) view.findViewById(R.id.ivVideo);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
        viewHolder.ibtnPlay = (ImageButton) view.findViewById(R.id.ibtnPlay);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        ImageView imageView = viewHolder.ivVideo;
        String url = "http://img.youtube.com/vi/" + cursor.getString(MovieDetailsFragment.COL_VIDEO_KEY) + "/1.jpg";
        Picasso.with(context).load(url).into(imageView);
        viewHolder.tvName.setText(cursor.getString(MovieDetailsFragment.COL_VIDEO_NAME));
        viewHolder.ibtnPlay.setTag(cursor.getString(MovieDetailsFragment.COL_VIDEO_KEY));
        viewHolder.ibtnPlay.setContentDescription(context.getResources().getString(R.string.video));
        viewHolder.ibtnPlay.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoListAdapterCallback != null) {
                    mVideoListAdapterCallback.onClickCallback((String) v.getTag());
                }
            }
        });
    }


    class ViewHolder {
        TextView tvName;
        ImageView ivVideo;
        ImageButton ibtnPlay;
    }

    /**
     * To send back video item click to MovieDetailsFragment
     */
    public interface VideoListAdapterCallback {
        void onClickCallback(String key);
    }
}
