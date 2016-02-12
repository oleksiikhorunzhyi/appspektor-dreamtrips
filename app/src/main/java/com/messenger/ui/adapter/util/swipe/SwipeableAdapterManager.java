package com.messenger.ui.adapter.util.swipe;

import android.support.v7.widget.RecyclerView;

public class SwipeableAdapterManager<A extends RecyclerView.Adapter & SwipeableWrapperAdapter.SwipeLayoutContainer> {

    private SwipeableWrapperAdapter wrapperAdapter;

    public RecyclerView.Adapter provideWrappedAdapter(A adapter) {
        return wrapperAdapter = new SwipeableWrapperAdapter(adapter);
    }
    public void closeAllItems() {
        wrapperAdapter.closeAllItems();
    }
}
