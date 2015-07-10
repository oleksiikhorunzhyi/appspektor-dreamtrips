package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import javax.inject.Inject;
import javax.inject.Provider;

@Layout(R.layout.adapter_item_feed_trip_event)
public class FeedTripEventCell extends FeedHeaderCell<FeedTripEventModel> {

    @Inject
    Provider<Injector> injectorProvider;
    @Inject
    ActivityRouter router;
    protected SessionHolder<UserSession> appSessionHolder;

    TripCell tripCell;

    public FeedTripEventCell(View view) {
        super(view);
        tripCell = new TripCell(view);
        injectorProvider.get().inject(tripCell);
    }

    @Override
    public void fillWithItem(FeedTripEventModel item) {
        super.fillWithItem(item);
        tripCell.fillWithItem(item.getEntities()[0]);

        itemView.setOnClickListener(view -> router.openTripDetails(getModelObject().getEntities()[0]));
    }

    @Override
    public void prepareForReuse() {

    }
}
