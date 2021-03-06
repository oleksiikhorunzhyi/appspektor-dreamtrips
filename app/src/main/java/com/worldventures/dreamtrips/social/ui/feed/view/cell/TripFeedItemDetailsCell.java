package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TripFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_trip_event)
public class TripFeedItemDetailsCell extends FeedItemDetailsCell<TripFeedItem, BaseFeedCell.FeedCellDelegate<TripFeedItem>> {

   @Inject SessionHolder appSessionHolder;

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
      router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(FeedItem.create(feedItem.getItem(), null))
                  .showAdditionalInfo(true)
                  .build())
            .build());
   }
}
