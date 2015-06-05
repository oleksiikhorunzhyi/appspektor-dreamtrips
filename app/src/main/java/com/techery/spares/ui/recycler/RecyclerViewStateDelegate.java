package com.techery.spares.ui.recycler;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewStateDelegate {

    private static final String KEY = "classname.recycler.layout";
    private RecyclerView recyclerView;
    private Parcelable savedRecyclerLayoutState;

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable("classname.recycler.layout");
        }
    }

    public void saveStateIfNeeded(Bundle outState) {
        if (savedRecyclerLayoutState == null && recyclerView != null) {
            savedRecyclerLayoutState = recyclerView.getLayoutManager().onSaveInstanceState();
        }
        if (savedRecyclerLayoutState != null) {
            outState.putParcelable(KEY, savedRecyclerLayoutState);
        }
    }

    public void restoreStateIfNeeded() {
        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
            throw new IllegalStateException("RecyclerView or LayoutManager is not set");
        }
        if (savedRecyclerLayoutState != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            savedRecyclerLayoutState = null;
        }
    }

    public void onDestroyView() {
        savedRecyclerLayoutState = recyclerView.getLayoutManager().onSaveInstanceState();
        recyclerView = null;
    }
}

