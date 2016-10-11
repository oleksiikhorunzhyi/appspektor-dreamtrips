package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.content.Context;
import android.location.Location;
import android.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.LocationSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.map.DtlMapPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants.DtlMerchantsPath;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import flow.path.Path;
import icepick.State;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class DtlLocationChangePresenterImpl extends DtlPresenterImpl<DtlLocationChangeScreen, ViewState.EMPTY> implements DtlLocationChangePresenter {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject MerchantsInteractor merchantInteractor;
   //
   @State ScreenMode screenMode = ScreenMode.NEARBY_LOCATIONS;
   @State ArrayList<DtlExternalLocation> dtlNearbyLocations = new ArrayList<>();
   @State boolean toolbarInitialized;
   //
   private Subscription locationRequestNoFallback;
   //
   private AtomicBoolean noMerchants = new AtomicBoolean(Boolean.FALSE);

   public DtlLocationChangePresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (getView().isTabletLandscape()) {
         // this path is not applicable for tablet landscape - it is embedded in DtlToolbar
         navigateToPath(DtlMerchantsPath.getDefault());
         return;
      }
      apiErrorPresenter.setView(getView());
      //
      tryHideNearMeButton();
      // remember this observable - we will start listening to search below only after this fires
      Observable<DtlLocation> locationObservable = connectDtlLocationUpdate();
      //
      connectNearbyLocations();
      connectEmptyMerchantsObservable();
      connectLocationsSearch();
      connectLocationDelegateNoFallback();
      connectToolbarMapClicks();
      connectToolbarCollapses();
      connectToolbarLocationSearch(locationObservable.take(1));
   }

   private void connectToolbarMapClicks() {
      getView().provideMapClickObservable().compose(bindView()).subscribe(aVoid -> mapClicked());
   }

   private void connectToolbarCollapses() {
      getView().provideDtlToolbarCollapsesObservable()
            .compose(bindView())
            .subscribe(aVoid -> navigateToPath(DtlMerchantsPath.getDefault()));
      // below: treat merchant search input focus gain as location search exit (collapsing)
      getView().provideMerchantInputFocusLossObservable()
            .compose(bindView())
            .subscribe(aVoid -> navigateToPath(DtlMerchantsPath.getDefault()));
   }

   private void connectToolbarLocationSearch(Observable<DtlLocation> dtlLocationObservable) {
      getView().provideLocationSearchObservable()
            .skipUntil(dtlLocationObservable)
            .debounce(250L, TimeUnit.MILLISECONDS)
            .compose(bindView())
            .subscribe(this::search);
   }

   private Observable<DtlLocation> connectDtlLocationUpdate() {
      Observable<DtlLocation> locationObservable = locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .map(DtlLocationCommand::getResult)
            .compose(bindViewIoToMainComposer());
      Observable.combineLatest(locationObservable, filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery), Pair::new).take(1).subscribe(pair -> {
         getView().updateToolbarTitle(pair.first, pair.second);
         toolbarInitialized = true;
      });
      return locationObservable;
   }

   private void connectLocationDelegateNoFallback() {
      locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .timeout(10L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe(this::onLocationObtained, throwable -> {
               if (throwable instanceof TimeoutException) getView().hideProgress();
            });
   }

   private void onLocationObtained(Location location) {
      switch (screenMode) {
         case NEARBY_LOCATIONS:
            locationInteractor.nearbyLocationPipe().send(new DtlNearbyLocationAction(location));
            break;
         case AUTO_NEAR_ME:
            DtlLocation dtlLocation = ImmutableDtlManualLocation.builder()
                  .locationSourceType(LocationSourceType.NEAR_ME)
                  .longName(context.getString(R.string.dtl_near_me_caption))
                  .coordinates(new com.worldventures.dreamtrips.modules.trips.model.Location(location))
                  .build();
            locationInteractor.change(dtlLocation);
            navigateToPath(DtlMerchantsPath.withAllowedRedirection());
            break;
         case SEARCH:
            break;
      }
   }

   @Override
   public void loadNearMeRequested() {
      screenMode = ScreenMode.AUTO_NEAR_ME;
      //
      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
         locationRequestNoFallback.unsubscribe();
      //
      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .doOnSubscribe(getView()::showProgress)
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   private void tryHideNearMeButton() {
      locationInteractor.locationPipe()
            .observeSuccessWithReplay()
            .filter(command -> command.getResult().getLocationSourceType() == LocationSourceType.NEAR_ME)
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> getView().hideNearMeButton());
   }

   private void connectLocationsSearch() {
      locationInteractor.searchLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlSearchLocationAction>().onStart(command -> getView().showProgress())
                  .onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(this::onSearchFinished));
   }

   private void onSearchFinished(DtlSearchLocationAction action) {
      getView().setItems(action.getResult(), false);
   }

   private void mapClicked() {
      History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext())));
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   private void navigateToPath(Path path) {
      clearCacheBeforeCloseScreen();
      //
      History history = History.single(path);
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   private void clearCacheBeforeCloseScreen() {
      locationInteractor.searchLocationPipe().clearReplays();
      locationInteractor.nearbyLocationPipe().clearReplays();
   }

   private void search(String query) {
      screenMode = ScreenMode.SEARCH;
      locationInteractor.searchLocationPipe().cancelLatest();
      locationInteractor.searchLocationPipe().send(new DtlSearchLocationAction(query.trim()));
   }

   @Override
   public void onLocationResolutionGranted() {
      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
         locationRequestNoFallback.unsubscribe();
      //
      gpsLocationDelegate.requestLocationUpdate()
            .compose(new IoToMainComposer<>())
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   @Override
   public void onLocationResolutionDenied() {
      getView().hideProgress();
   }

   /**
    * Check if given error's cause is insufficient GPS resolution
    * or usual throwable and act accordingly
    *
    * @param e exception that {@link LocationDelegate} returned
    */
   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException)
         getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
      else onLocationResolutionDenied();
   }

   private void connectNearbyLocations() {
      locationInteractor.nearbyLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DtlNearbyLocationAction>().onStart(command -> getView().showProgress())
                  .onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(this::onLocationsLoaded));
   }

   private void onLocationsLoaded(DtlNearbyLocationAction action) {
      getView().hideProgress();
      showLoadedLocations(action.getResult());
   }

   private void showLoadedLocations(List<DtlExternalLocation> locations) {
      dtlNearbyLocations.clear();
      dtlNearbyLocations.addAll(locations);
      getView().switchVisibilityNoMerchants(noMerchants.get());
      getView().switchVisibilityOrCaption(noMerchants.get() && !locations.isEmpty());
      getView().setItems(locations, !locations.isEmpty());
   }

   private void connectEmptyMerchantsObservable() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .map(List::isEmpty)
            .subscribe(noMerchants::set);
   }

   @Override
   public void locationSelected(DtlExternalLocation dtlExternalLocation) {
      locationInteractor.searchLocationPipe().clearReplays();
      merchantInteractor.thinMerchantsHttpPipe().clearReplays();
      analyticsInteractor.dtlAnalyticsCommandPipe().send(DtlAnalyticsCommand.create(LocationSearchEvent.create(dtlExternalLocation)));
      locationInteractor.change(dtlExternalLocation);
      navigateToPath(DtlMerchantsPath.getDefault());
   }

   /**
    * Represents view state of presenter+view - to be used for screen restoration, e.g. after rotate.
    */
   public enum ScreenMode {
      /**
       * System tried to pre-load some locations based on device's current GPS location.<br />
       * Default for current screen.
       */
      NEARBY_LOCATIONS,
      /**
       * User explicitly requested to load merchants by device's GPS location.
       */
      AUTO_NEAR_ME,
      /**
       * User requested search and is currently viewing results / waiting for progress.
       */
      SEARCH,
   }
}
