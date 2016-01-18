package com.worldventures.dreamtrips.modules.feed.view.fragment;

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
