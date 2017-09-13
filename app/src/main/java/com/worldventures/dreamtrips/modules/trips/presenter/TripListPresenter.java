package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
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
import com.worldventures.dreamtrips.modules.trips.service.analytics.TripItemAnalyticAction;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsAdobeAnalyticAction;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsApptentiveAnalyticAction;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewMapDreamTripsAnalyticAction;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewTripDetailsAnalyticAction;

import java.util.List;

import javax.inject.Inject;

import icepick.State;
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

   @Override
   public void onResume() {
      super.onResume();
      analyticsInteractor.analyticsActionPipe().send(new ViewDreamTripsAdobeAnalyticAction());
   }

   public void takeView(View view) {
      super.takeView(view);
      analyticsInteractor.analyticsActionPipe().send(new ViewDreamTripsApptentiveAnalyticAction());
      subscribeToLoadTrips();
      subscribeToFilterEvents();
      subscribeToLikesChanges();

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
      sendAnalyticAction(TripItemAnalyticAction.likeAction(trip.getTripId(), trip.getName()));
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
      sendAnalyticAction(TripItemAnalyticAction.addToBucketListAction(tripModel.getTripId(), tripModel.getName()));
      if (!tripModel.isInBucketList()) {
         bucketInteractor.createPipe()
               .createObservable(new CreateBucketItemCommand(ImmutableBucketBodyImpl.builder()
                     .type("trip")
                     .id(tripModel.getTripId())
                     .build()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<CreateBucketItemCommand>()
                     .onSuccess(createBucketItemCommand -> {
                        tripModel.setInBucketList(true);
                        view.dataSetChanged();
                        view.showItemAddedToBucketList(createBucketItemCommand.getResult());
                     })
                     .onFail((createBucketItemCommand, throwable) -> {
                        tripModel.setInBucketList(!tripModel.isInBucketList());
                        handleError(createBucketItemCommand, throwable);
                     }));
      } else {
         tripModel.setInBucketList(!tripModel.isInBucketList());
         view.dataSetChanged();
         view.showErrorMessage();
      }
   }

   public void openMap() {
      sendAnalyticAction(new ViewMapDreamTripsAnalyticAction());
      view.openMap();
   }

   public void openTrip(TripModel tripModel) {
      view.moveToTripDetails(tripModel);

      tripFilterEventDelegate.last()
            .subscribe(tripsFilterData -> {
               ViewTripDetailsAnalyticAction analyticAction = (new ViewTripDetailsAnalyticAction(tripModel.getTripId(),
                     tripModel.getName(), view.isSearchOpened() ? query : "", new TripsFilterDataAnalyticsWrapper(tripsFilterData)));
               sendAnalyticAction(analyticAction);
            });
   }

   public void sendAnalyticAction(BaseAnalyticsAction action) {
      analyticsInteractor.analyticsActionPipe().send(action);
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

      void openMap();
   }
}
