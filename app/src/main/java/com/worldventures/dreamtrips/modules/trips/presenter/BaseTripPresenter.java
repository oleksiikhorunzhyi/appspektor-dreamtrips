package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

   @Inject BucketInteractor bucketInteractor;
   @Inject FeedInteractor feedInteractor;

   protected TripModel trip;

   public BaseTripPresenter(TripModel trip) {
      this.trip = trip;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      subscribeToLikesChanges();
   }

   @Override
   public void onResume() {
      super.onResume();
      view.setup(trip);
   }

   public void addTripToBucket() {
      bucketInteractor.createPipe()
            .createObservable(new CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                  .type("trip")
                  .id(trip.getTripId())
                  .build()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CreateBucketItemCommand>()
                  .onSuccess(createBucketItemCommand -> {
                     trip.setInBucketList(true);
                     view.setup(trip);
                     view.tripAddedToBucketItem(createBucketItemCommand.getResult());
                  })
                  .onFail(this::handleError));
   }

   public void likeTrip() {
      feedInteractor.changeFeedEntityLikedStatusPipe().send(new ChangeFeedEntityLikedStatusCommand(trip));
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(this::likeStatusChanged)
                  .onFail(this::handleError));
   }

   private void likeStatusChanged(ChangeFeedEntityLikedStatusCommand command) {
      if (command.getResult().getUid().equals(trip.getUid())) {
         trip.syncLikeState(command.getResult());
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