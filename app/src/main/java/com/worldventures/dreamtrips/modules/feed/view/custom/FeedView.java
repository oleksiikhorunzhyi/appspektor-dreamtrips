package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPostEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedAvatarEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedCoverEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPostEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedUndefinedEventCell;
import com.worldventures.dreamtrips.modules.profile.ReloadFeedModel;
import com.worldventures.dreamtrips.modules.profile.view.cell.ProfileCell;
import com.worldventures.dreamtrips.modules.profile.view.cell.ReloadFeedCell;

public class FeedView extends RecyclerView {

    private IgnoreFirstItemAdapter adapter;
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

    public void setup(Bundle savedInstanceState, IgnoreFirstItemAdapter adapter) {
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);

        this.adapter = adapter;
        this.adapter.registerCell(User.class, ProfileCell.class);
        this.adapter.registerCell(ReloadFeedModel.class, ReloadFeedCell.class);

        this.adapter.registerCell(FeedAvatarEventModel.class, FeedAvatarEventCell.class);
        this.adapter.registerCell(FeedCoverEventModel.class, FeedCoverEventCell.class);

        this.adapter.registerCell(FeedPhotoEventModel.class, FeedPhotoEventCell.class);
        this.adapter.registerCell(FeedTripEventModel.class, FeedTripEventCell.class);
        this.adapter.registerCell(FeedBucketEventModel.class, FeedBucketEventCell.class);
        this.adapter.registerCell(FeedPostEventModel.class, FeedPostEventCell.class);

        this.adapter.registerCell(FeedUndefinedEventModel.class, FeedUndefinedEventCell.class);

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

    @Override
    public BaseArrayListAdapter<BaseFeedModel> getAdapter() {
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
