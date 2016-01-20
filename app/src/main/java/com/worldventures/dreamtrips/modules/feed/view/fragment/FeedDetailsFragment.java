package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.ui.fragment.FragmentHelper;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedDetailsPresenter;

public abstract class FeedDetailsFragment<PRESENTER extends FeedDetailsPresenter> extends CommentableFragment<PRESENTER, FeedDetailsBundle> implements FeedDetailsPresenter.View {

    private int loadMoreOffset;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        //
        registerCells();

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateStickyInputContainerState();
            }
        });
        recyclerView.setAdapterDataCallback(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                updateStickyInputContainerState();
            }
        });
    }

    private void updateStickyInputContainerState() {
        View view = layout.findViewByPosition(0);
        if (layout.findFirstVisibleItemPosition() > 0 || isNeedToShowInputPanel(view)) {
            inputContainer.setVisibility(View.VISIBLE);
        } else {
            inputContainer.setVisibility(View.GONE);
        }
    }

    private boolean isNeedToShowInputPanel(View view) {
        if (view == null) return false;
        //
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        Rect screenRect = new Rect();
        getActivity().getWindow().getDecorView().getGlobalVisibleRect(screenRect);

        return (location[1] + view.getHeight() + inputContainer.getHeight()) <= screenRect.bottom;
    }

    protected abstract void registerCells();

    @Override
    public void onDestroyView() {
        FragmentHelper.resetChildFragmentManagerField(this);
        //
        super.onDestroyView();
    }

    @Override
    public void setFeedItem(FeedItem feedItem) {
        adapter.addItem(0, feedItem);
        adapter.notifyItemInserted(0);
        loadMoreOffset = 1;
    }

    @Override
    public void updateFeedItem(FeedItem feedItem) {
        adapter.updateItem(feedItem);
    }

    @Override
    protected int getAdditionalItemsCount() {
        return super.getAdditionalItemsCount() + loadMoreOffset;
    }

    @Override
    protected int getLoadMorePosition() {
        return super.getLoadMorePosition() + loadMoreOffset;
    }
}
