package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedAvatarEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedBucketEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedCoverEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedPhotoEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.model.FeedUndefinedEventModel;
import com.worldventures.dreamtrips.modules.feed.view.adapter.HeaderLayoutManagerFixed;
import com.worldventures.dreamtrips.modules.feed.view.adapter.ParallaxRecyclerAdapter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedAvatarEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedBucketEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedCoverEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedTripEventCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedUndefinedEventCell;

import javax.inject.Provider;

public class FeedView extends RecyclerView {

    private ParallaxRecyclerAdapter<BaseFeedModel> adapter;
    private RecyclerViewStateDelegate stateDelegate;
    private HeaderLayoutManagerFixed layoutManagerFixed;

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

    public void setup(Provider<Injector> injectorProvider, Bundle savedInstanceState) {
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);

        adapter = new ParallaxRecyclerAdapter<>(getContext(), injectorProvider);
        adapter.registerCell(FeedAvatarEventModel.class, FeedAvatarEventCell.class);
        adapter.registerCell(FeedCoverEventModel.class, FeedCoverEventCell.class);

        adapter.registerCell(FeedPhotoEventModel.class, FeedPhotoEventCell.class);
        adapter.registerCell(FeedTripEventModel.class, FeedTripEventCell.class);
        adapter.registerCell(FeedBucketEventModel.class, FeedBucketEventCell.class);

        adapter.registerCell(FeedUndefinedEventModel.class, FeedUndefinedEventCell.class);

        layoutManagerFixed = new HeaderLayoutManagerFixed(getContext());
        setLayoutManager(layoutManagerFixed);
        adapter.setShouldClipView(false);

        setAdapter(adapter);

        stateDelegate.setRecyclerView(this);

    }

    @Override
    public HeaderLayoutManagerFixed getLayoutManager() {
        return layoutManagerFixed;
    }

    public void setHeader(View header) {
        layoutManagerFixed.setHeaderIncrementFixer(header);
        adapter.setParallaxHeader(header, this);
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
    public LoaderRecycleAdapter<BaseFeedModel> getAdapter() {
        return adapter;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        // check if scrolling up
        if (direction < 1) {
            boolean original = super.canScrollVertically(direction);
            return !original && getChildAt(0) != null && getChildAt(0).getTop() < 0 || original;
        }
        return super.canScrollVertically(direction);

    }
}
