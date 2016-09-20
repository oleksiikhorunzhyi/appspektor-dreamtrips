package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;

import javax.inject.Inject;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemDetailsCell extends FeedItemDetailsCell<TripFeedItem, CellDelegate<TripFeedItem>> {

   @Inject SessionHolder<UserSession> appSessionHolder;

   private TripFeedViewInjector tripFeedViewInjector;

   public TripFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      tripFeedViewInjector = new TripFeedViewInjector(itemView);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      tripFeedViewInjector.initTripData(getModelObject().getItem());
   }
}
