package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.FeedTripEventModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedHeaderCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.TripCell;

import javax.inject.Inject;
import javax.inject.Provider;

@Layout(R.layout.adapter_item_feed_undefined_event)
public class FeedUndefinedEventCell extends FeedHeaderCell<FeedTripEventModel> {



    public FeedUndefinedEventCell(View view) {
        super(view);
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
    }
}
