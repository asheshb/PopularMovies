package com.bitwindow.popularmovies;

/**
 * Created by ashbey on 9/27/2015.
 * To show movie videos in gridview without scroll bar wrapping to the height of contents in movie details fragment
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Use this class when you want a gridview that doesn't scroll and automatically
 * wraps to the height of its contents
 */
public class WrappedGridView extends GridView {
    private boolean expanded = false;

    public WrappedGridView(Context context) {
        super(context);
    }

    public WrappedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrappedGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (isExpanded()) {
            // Calculate entire height by providing a very large height hint.
            // View.MEASURED_SIZE_MASK represents the largest height possible.
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);

            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}