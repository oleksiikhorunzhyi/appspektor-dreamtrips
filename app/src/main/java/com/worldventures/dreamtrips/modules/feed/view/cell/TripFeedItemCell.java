package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import javax.inject.Inject;
import javax.inject.Provider;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemCell extends FeedItemCell<TripFeedItem> {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    TripCell tripCell;

    public TripFeedItemCell(View view) {
        super(view);
        tripCell = new TripCell(view);
    }

    @Override
    public void afterInject() {
        super.afterInject();
        tripCell.setEventBus(getEventBus());
        injectorProvider.get().inject(tripCell);
    }

    @Override
    public void fillWithItem(TripFeedItem item) {
        super.fillWithItem(item);
        tripCell.fillWithItem(item.getItem());
    }

    @Override
    protected void openItemDetails() {
        tripCell.actionItemClick();
        getEventBus().post(new FeedItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, getModelObject().getItem().getUid(),
                FeedEntityHolder.Type.TRIP));
    }

    @Override
    protected void onMore() {

    }

    @Override
    public void prepareForReuse() {

    }
}
