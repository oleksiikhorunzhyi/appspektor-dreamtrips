package com.worldventures.dreamtrips.modules.trips.view.util;

import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.widget.CheckedTextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.event.TripItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class TripFeedViewInjector extends TripViewInjector {

   @InjectView(R.id.imageViewTripImage) protected SimpleDraweeView imageViewTripImage;
   @InjectView(R.id.imageViewLike) protected CheckedTextView likeView;
   @InjectView(R.id.imageViewAddToBucket) protected CheckedTextView addToBucketView;

   private TripModel tripModel;
   private SyncStateListener syncStateListener;

   private Router router;
   private EventBus eventBus;

   public TripFeedViewInjector(View rootView, Router router, EventBus eventBus) {
      super(rootView);
      this.router = router;
      this.eventBus = eventBus;
   }

   @Override
   public void initTripData(TripModel tripModel, User currentUser) {
      super.initTripData(tripModel, currentUser);
      this.tripModel = tripModel;
      //
      likeView.setChecked(tripModel.isLiked());
      addToBucketView.setChecked(tripModel.isInBucketList());
      addToBucketView.setEnabled(!tripModel.isInBucketList());

      PointF pointF = new PointF(0.5f, 0.0f);
      imageViewTripImage.getHierarchy().setActualImageFocusPoint(pointF);
      imageViewTripImage.setImageURI(Uri.parse(tripModel.getThumb(imageViewTripImage.getResources())));
   }

   @OnClick(R.id.imageViewLike)
   void onLike() {
      syncUIStateWithModel();
      eventBus.post(new LikeTripPressedEvent(tripModel));
      eventBus.post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_LIKE, tripModel.getTripId(), tripModel.getName()));
   }

   @OnClick(R.id.imageViewAddToBucket)
   void onAddToBucket() {
      tripModel.setInBucketList(true);
      syncUIStateWithModel();
      eventBus.post(new AddToBucketEvent(tripModel));
      eventBus.post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_ADD_TO_BUCKET_LIST, tripModel.getTripId(), tripModel
            .getName()));
   }

   @OnClick(R.id.itemLayout)
   void actionItemClick() {
      router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(FeedItem.create(tripModel, null))
                  .showAdditionalInfo(true)
                  .build())
            .build());

      eventBus.post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, tripModel.getTripId(), tripModel.getName()));
   }

   @OnClick(R.id.layoutInfo)
   void onInfoClick() {
      actionItemClick();
   }

   private void syncUIStateWithModel() {
      if (syncStateListener != null) syncStateListener.syncStateWithModel();
   }

   public void setSyncStateListener(SyncStateListener syncStateListener) {
      this.syncStateListener = syncStateListener;
   }

   public interface SyncStateListener {

      void syncStateWithModel();
   }
}
