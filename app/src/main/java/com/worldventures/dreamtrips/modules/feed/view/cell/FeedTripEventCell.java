package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

@Layout(R.layout.adapter_item_feed_trip_event)
public class FeedTripEventCell extends FeedHeaderCell<FeedTripEventModel> {
    TripCell tripCell;

    public FeedTripEventCell(View view) {
        super(view);
        tripCell = new TripCell(view);
    }

    @Override
    public void fillWithItem(FeedTripEventModel item) {
        super.fillWithItem(item);
        //  tripCell.fillWithItem(null/*TODO*/);
    }

    @Override
    public void prepareForReuse() {

    }
}
