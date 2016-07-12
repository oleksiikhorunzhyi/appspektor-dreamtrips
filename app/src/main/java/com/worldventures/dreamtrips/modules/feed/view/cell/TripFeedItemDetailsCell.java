package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;

import javax.inject.Inject;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemDetailsCell extends FeedItemDetailsCell<TripFeedItem> {

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    TripFeedViewInjector tripFeedViewInjector;

    public TripFeedItemDetailsCell(View view) {
        super(view);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        tripFeedViewInjector = new TripFeedViewInjector(itemView, router, getEventBus());
        tripFeedViewInjector.setSyncStateListener(this::syncUIStateWithModel);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        tripFeedViewInjector.initTripData(getModelObject().getItem(), appSessionHolder.get().get().getUser());
    }

    @Override
    protected void onMore() {

    }

    @Override
    public void prepareForReuse() {

    }
}
