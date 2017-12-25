package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;
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
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class TripListPresenter extends Presenter<TripListPresenter.View> {

   @Inject FeedInteractor feedInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject TripFilterEventDelegate tripFilterEventDelegate;
   @Inject ResetFilterEventDelegate resetFilterEventDelegate;
   @Inject AppConfigurationInteractor appConfigurationInteractor;
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
      Observable.combineLatest(appConfigurationInteractor.getConfigurationPipe()
                  .observe()
                  .filter(state -> state.status == ActionState.Status.SUCCESS)
                  .map(state -> state.action.getResult()),
            tripsInteractor.tripsPipe()
                  .observe()
                  .filter(state -> state.status == ActionState.Status.SUCCESS || state.status == ActionState.Status.PROGRESS)
                  .map(state -> state.action.getItems()),
            (configuration, trips) -> {
               List<Object> items = new ArrayList<>();
               if (configuration.getTravelBannerRequirement() != null
                     && configuration.getTravelBannerRequirement().getEnabled()) {
                  items.add(configuration.getTravelBannerRequirement());
               }
               items.addAll(trips);
               return items;
            })
            .compose(bindViewToMainComposer())
            .subscribe(view::itemsChanged);


      reload();
      appConfigurationInteractor.getConfigurationPipe().send(new ConfigurationCommand());
   }

   public void reload() {
      if (view == null) {
         return;
      }

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
                  .onSuccess(getTripsCommand -> {
                     loading = false;
                     noMoreItems = !getTripsCommand.hasMore();
                  })
                  .onFinish(command -> view.finishLoading())
                  .onFail(this::handleError));
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

   public void hideTripRequirement() {
      appConfigurationInteractor.getConfigurationPipe().send(new ConfigurationCommand(null, true));
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
               ViewTripDetailsAnalyticAction analyticAction = new ViewTripDetailsAnalyticAction(tripModel.getTripId(),
                     tripModel.getName(), view.isSearchOpened() ? query : "", new TripsFilterDataAnalyticsWrapper(tripsFilterData));
               sendAnalyticAction(analyticAction);
            });
   }

   private void sendAnalyticAction(BaseAnalyticsAction action) {
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

      void itemsChanged(List<Object> items);

      void itemLiked(FeedEntity feedEntity);

      boolean isSearchOpened();

      void showItemAddedToBucketList(BucketItem bucketItem);

      void moveToTripDetails(TripModel tripModel);

      void openMap();
   }
}
