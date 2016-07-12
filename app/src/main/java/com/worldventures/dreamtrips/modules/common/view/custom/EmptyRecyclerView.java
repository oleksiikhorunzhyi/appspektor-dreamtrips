package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {

    private AdapterDataObserver adapterDataCallback;

    protected final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
            if (adapterDataCallback != null) adapterDataCallback.onChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            checkIfEmpty();
            if (adapterDataCallback != null) adapterDataCallback.onItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkIfEmpty();
            if (adapterDataCallback != null) adapterDataCallback.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            checkIfEmpty();
            if (adapterDataCallback != null) adapterDataCallback.onItemRangeMoved(fromPosition, toPosition, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkIfEmpty();
            if (adapterDataCallback != null) adapterDataCallback.onItemRangeRemoved(positionStart, itemCount);
        }
    };

    protected View emptyView;

    public EmptyRecyclerView(Context context) {
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            if (getAdapter().getItemCount() > 0) {
                hideEmptyView();
            } else {
                showEmptyView();
            }
        }
    }

    public void showEmptyView() {
        emptyView.setVisibility(VISIBLE);
        setVisibility(GONE);
    }

    public void hideEmptyView() {
        emptyView.setVisibility(GONE);
        setVisibility(VISIBLE);
    }

    public boolean isEmptyViewVisible() {
        return emptyView != null && emptyView.getVisibility() == VISIBLE;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && oldAdapter.hasObservers()) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
        checkIfEmpty();
    }

    public void setAdapterDataCallback(AdapterDataObserver adapterDataCallback) {
        this.adapterDataCallback = adapterDataCallback;
    }
}