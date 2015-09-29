package com.bitwindow.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by ashbey on 9/29/2015.
 * To show movie videos in listview without scroll bar wrapping to the height of contents in movie details fragment
 */
public class WrappedListView extends ListView
{

    private boolean expanded = false;

    public WrappedListView(Context context)
    {
        super(context);
    }

    public WrappedListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WrappedListView(Context context, AttributeSet attrs,int defStyle)
    {
        super(context, attrs, defStyle);
    }

    private boolean isExpanded()
    {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        if (isExpanded())
        {
            // Calculate entire height by providing a very large height hint.
            // But do not use the highest 2 bits of this integer; those are
            // reserved for the MeasureSpec mode.
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        }
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}