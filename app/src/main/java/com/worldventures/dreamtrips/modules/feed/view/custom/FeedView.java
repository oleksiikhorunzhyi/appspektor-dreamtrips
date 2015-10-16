package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.UndefinedFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.BucketFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PostFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.TripFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.UndefinedFeedItemCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.profile.model.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ReloadFeedCell;

public class FeedView extends EmptyRecyclerView {

    private BaseArrayListAdapter adapter;

    private RecyclerViewStateDelegate stateDelegate;
    private OffsetYListener offsetYListener;
    private LinearLayoutManager layoutManager;

    public FeedView(Context context) {
        this(context, null);
    }

    public FeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void onSaveInstanceState(Bundle outState) {
        stateDelegate.saveStateIfNeeded(outState);
    }

    public void setup(Bundle savedInstanceState, BaseArrayListAdapter adapter) {
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);

        this.adapter = adapter;
        adapter.registerCell(User.class, ProfileCell.class);
        adapter.registerCell(ReloadFeedModel.class, ReloadFeedCell.class);

        adapter.registerCell(PhotoFeedItem.class, PhotoFeedItemCell.class);
        adapter.registerCell(TripFeedItem.class, TripFeedItemCell.class);
        adapter.registerCell(BucketFeedItem.class, BucketFeedItemCell.class);
        adapter.registerCell(PostFeedItem.class, PostFeedItemCell.class);

        adapter.registerCell(UndefinedFeedItem.class, UndefinedFeedItemCell.class);
        adapter.registerCell(LoadMoreModel.class, LoaderCell.class);

        layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);

        setAdapter(this.adapter);

        stateDelegate.setRecyclerView(this);


        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (offsetYListener != null)
                    offsetYListener.onScroll(FeedView.this.computeVerticalScrollOffset());
            }
        });
    }

    public void restoreStateIfNeeded() {
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stateDelegate.onDestroyView();
    }

    public BaseArrayListAdapter<FeedItem> getAdapter() {
        return adapter;
    }

    public void setOffsetYListener(OffsetYListener offsetYListener) {
        this.offsetYListener = offsetYListener;
    }

    public float getScrollOffset() {
        return FeedView.this.computeVerticalScrollOffset();
    }

    public interface OffsetYListener {
        void onScroll(int yOffset);
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }
}
