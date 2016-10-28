package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripsCommand;
import com.worldventures.dreamtrips.modules.trips.delegate.ResetFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.delegate.TripFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.CancelException;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class TripListPresenter extends Presenter<TripListPresenter.View> {

   @Inject FeedInteractor feedInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject TripFilterEventDelegate tripFilterEventDelegate;
   @Inject ResetFilterEventDelegate resetFilterEventDelegate;
   @Inject TripsInteractor tripsInteractor;

   @State String query;

   private boolean loadWithStatus;

   private boolean loading;
   private boolean noMoreItems = false;

   public TripListPresenter() {
   }

   public void takeView(View view) {
      super.takeView(view);
      TrackingHelper.dreamTrips(getAccountUserId());
      subscribeToLoadTrips();
      subscribeToFilterEvents();
      subscribeToLikesChanges();
   }

   @Override
   public void onResume() {
      super.onResume();
      loadWithStatus = true;
      reload();
   }

   public void reload() {
      if (view == null) return;

      loading = true;
      noMoreItems = false;
      if (loadWithStatus) {
         view.startLoading();
      }

      loadTrips(true);
   }

   public void resetFilters() {
      resetFilterEventDelegate.post(null);
   }

   private void loadMore() {
      loadTrips(false);
   }

   private void loadTrips(boolean refresh) {
      tripFilterEventDelegate.last()
            .subscribe(tripsFilterData -> tripsInteractor.tripsPipe()
                  .send(new GetTripsCommand(query, tripsFilterData, refresh)));
   }

   public void onMenuInflated() {
      subscribeToSearch();
   }

   private void subscribeToSearch() {
      view.textChanges()
            .compose(bindView())
            .subscribe(this::search);
   }

   private void search(String query) {
      this.query = query;
      tripsInteractor.tripsPipe().cancelLatest();
      reload();
   }

   private void subscribeToFilterEvents() {
      tripFilterEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(tripsFilterData -> reload());
   }

   private void subscribeToLoadTrips() {
      tripsInteractor.tripsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripsCommand>()
                  .onProgress((action, progress) -> view.itemsChanged(action.getItems()))
                  .onSuccess(getTripsCommand -> {
                     loading = false;
                     noMoreItems = !getTripsCommand.hasMore();
                     view.finishLoading();
                     view.itemsChanged(getTripsCommand.getItems());
                  })
                  .onFail((getTripsCommand, throwable) -> {
                     this.handleError(getTripsCommand, throwable);
                     view.finishLoading();
                  }));
   }

   public void likeItem(TripModel trip) {
      trackAction(trip, TrackingHelper.ATTRIBUTE_LIKE);
      feedInteractor.changeFeedEntityLikedStatusPipe().send(new ChangeFeedEntityLikedStatusCommand(trip));
   }

   private void subscribeToLikesChanges() {
      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(command -> view.itemLiked(command.getResult()))
                  .onFail(this::handleError));
   }

   public void addItemToBucket(TripModel tripModel) {
      trackAction(tripModel, TrackingHelper.ATTRIBUTE_ADD_TO_BUCKET_LIST);
      if (!tripModel.isInBucketList()) {
         bucketInteractor.createPipe()
               .createObservableResult(new CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                     .type("trip")
                     .id(tripModel.getTripId())
                     .build()))
               .map(CreateBucketItemCommand::getResult)
               .compose(bindViewToMainComposer())
               .subscribe(bucketItem -> {
                  tripModel.setInBucketList(true);
                  view.dataSetChanged();
                  view.showItemAddedToBucketList(bucketItem);
               }, throwable -> {
                  tripModel.setInBucketList(!tripModel.isInBucketList());
                  handleError(throwable);
               });
      } else {
         tripModel.setInBucketList(!tripModel.isInBucketList());
         view.dataSetChanged();
         view.showErrorMessage();
      }
   }

   public void openTrip(TripModel tripModel) {
      trackAction(tripModel, TrackingHelper.ATTRIBUTE_VIEW);
      view.moveToTripDetails(tripModel);
   }

   public void trackAction(TripModel tripModel, String actionAtrribute) {
      if (actionAtrribute.equals(TrackingHelper.ATTRIBUTE_VIEW)) {
         tripFilterEventDelegate.last()
               .subscribe(tripsFilterData -> {
                  TrackingHelper.viewTripDetails(tripModel.getTripId(), tripModel.getName(),
                        view.isSearchOpened() ? query : "", new TripsFilterDataAnalyticsWrapper(tripsFilterData));
               });
      } else {
         TrackingHelper.actionItemDreamtrips(actionAtrribute, tripModel.getTripId(), tripModel.getName());
      }
   }

   public void scrolled() {
      if (!loading && !noMoreItems) {
         loading = true;
         loadMore();
      }
   }

   public String getQuery() {
      return query;
   }

   public interface View extends RxView {

      void dataSetChanged();

      void showErrorMessage();

      void startLoading();

      void finishLoading();

      Observable<String> textChanges();

      void itemsChanged(List<TripModel> items);

      void itemLiked(FeedEntity feedEntity);

      boolean isSearchOpened();

      void showItemAddedToBucketList(BucketItem bucketItem);

      void moveToTripDetails(TripModel tripModel);
   }
}
