package com.worldventures.dreamtrips.modules.feed.view.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PaginationViewManager {

    private boolean loading = true;
    private boolean noMoreElements = false;

    private PaginationListener paginationListener;

    public PaginationViewManager(@NonNull RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int itemCount = recyclerView.getLayoutManager().getItemCount();
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findLastVisibleItemPosition();
                    if (!loading && !noMoreElements
                            && lastVisibleItemPosition == itemCount - 1) {
                        loading = true;
                        if(paginationListener != null) paginationListener.loadNext();
                    }
                }
            });
        }
    }

    public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
        this.loading = loading;
        this.noMoreElements = noMoreElements;
    }

    public boolean isNoMoreElements() {
        return noMoreElements;
    }

    public void setPaginationListener(PaginationListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    public interface PaginationListener {

        void loadNext();
    }
}
