package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Parcelable;
import android.util.Pair;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityShowEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.util.FeedEntityContentFragmentFactory;

import javax.inject.Inject;

public class FeedEntityDetailsPresenter extends FeedDetailsPresenter<FeedEntityDetailsPresenter.View> {

    @Inject
    FeedEntityContentFragmentFactory fragmentFactory;

    private boolean isSlave;

    public FeedEntityDetailsPresenter(FeedItem feedItem, boolean isSlave) {
        super(feedItem);
        this.isSlave = isSlave;
    }

    public void onEvent(FeedEntityShowEvent event) {
        if (!this.feedItem.equals(event.feedItem)) return;
        //
        Pair<Route, Parcelable> entityData = fragmentFactory.create(event.feedItem);
        /**
         * for bucket list tablet landscape orientation (slave mode)
         */
        if (feedItem.getType() == FeedEntityHolder.Type.BUCKET_LIST_ITEM) {
            ((BucketBundle) entityData.second).setSlave(isSlave);
        }
        view.showDetails(entityData.first, entityData.second);
    }

    @Override
    protected void back() {
        if (!isSlave) view.back();
    }

    public interface View extends FeedDetailsPresenter.View {

        void showDetails(Route route, Parcelable extra);
    }
}
