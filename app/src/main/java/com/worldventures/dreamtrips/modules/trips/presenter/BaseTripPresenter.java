package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

   @Inject BucketInteractor bucketInteractor;
   @Inject FeedEntityManager feedEntityManager;

   protected TripModel trip;

   public BaseTripPresenter(TripModel trip) {
      this.trip = trip;
   }

   public void onInjected() {
      super.onInjected();
      feedEntityManager.setRequestingPresenter(this);
   }

   @Override
   public void onResume() {
      super.onResume();
      view.setup(trip);
   }

   public void addTripToBucket() {
      view.bindUntilDropView(bucketInteractor.createPipe()
            .createObservableResult(new CreateBucketItemHttpAction(ImmutableBucketBodyImpl.builder()
                  .type("trip")
                  .id(trip.getTripId())
                  .build()))
            .map(CreateBucketItemHttpAction::getResponse)
            .observeOn(AndroidSchedulers.mainThread())).subscribe(bucketItem -> {
         trip.setInBucketList(true);
         view.setup(trip);
         view.tripAddedToBucketItem(bucketItem);
      }, this::handleError);
   }

   public void likeTrip() {
      if (!trip.isLiked()) {
         feedEntityManager.like(trip);
      } else {
         feedEntityManager.unlike(trip);
      }
   }

   public void onEvent(EntityLikedEvent event) {
      if (event.getFeedEntity().getUid().equals(trip.getUid())) {
         trip.syncLikeState(event.getFeedEntity());
         view.setup(trip);
         if (view.isVisibleOnScreen()) view.tripLiked(trip);
      }
   }

   public interface View extends RxView {
      void setup(TripModel tripModel);

      void tripAddedToBucketItem(BucketItem bucketItem);

      void tripLiked(TripModel tripModel);
   }
}