package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemDetailsCell extends FeedItemDetailsCell<TripFeedItem, BaseFeedCell.FeedCellDelegate<TripFeedItem>> {

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

   @OnClick(R.id.itemLayout)
   void onItemLayoutClicked() {
      openEntityDetails();
   }

   @Override
   protected void onOpenEntityDetails() {
      FeedItem feedItem = getModelObject();
      router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(FeedItem.create(feedItem.getItem(), null))
                  .showAdditionalInfo(true)
                  .build())
            .build());
   }
}
