package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import javax.inject.Inject;
import javax.inject.Provider;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemCell extends FeedHeaderCell<TripFeedItem> {

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
    protected void onDelete() {

    }

    @Override
    protected void onEdit() {

    }

    @Override
    protected void onMore() {

    }

    @Override
    public void prepareForReuse() {

    }
}
