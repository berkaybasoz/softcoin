package com.audacityit.finder.util;

import android.widget.AbsListView;

/**
 * Created by IS97100 on 22.07.2017.
 */

public abstract class ListViewScrollListener implements AbsListView.OnScrollListener {


    public abstract void onScrollUp();

    public abstract void onScrollDown();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    int last = 0;
    boolean control = true;

    @Override
    public void onScroll(AbsListView view, int current, int visibles, int total) {
        if (current < last && !control) {
            onScrollUp();
            control = true;
        } else if (current > last && control) {
            onScrollDown();
            control = false;
        }

        last = current;
    }
}
