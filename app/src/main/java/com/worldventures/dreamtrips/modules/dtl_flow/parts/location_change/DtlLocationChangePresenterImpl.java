package com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change;

import android.content.Context;
import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.LocationSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.NearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.ProxyApiErrorView;
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
import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class DtlLocationChangePresenterImpl extends DtlPresenterImpl<DtlLocationChangeScreen, ViewState.EMPTY>
      implements DtlLocationChangePresenter {

   @Inject LocationDelegate gpsLocationDelegate;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;
   @Inject DtlLocationInteractor locationInteractor;
   @Inject MerchantsInteractor merchantInteractor;

   @State ScreenMode screenMode = ScreenMode.NEARBY_LOCATIONS;
   @State ArrayList<DtlLocation> dtlNearbyLocations = new ArrayList<>();
   @State boolean toolbarInitialized;
   @State String merchantQuery;

   private Subscription locationRequestNoFallback;
   private AtomicBoolean noMerchants = new AtomicBoolean(Boolean.FALSE);

   public DtlLocationChangePresenterImpl(Context context, Injector injector, String merchantQuery) {
      super(context);
      this.merchantQuery = merchantQuery;
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
      apiErrorViewAdapter.setView(new ProxyApiErrorView(getView(), () -> getView().hideProgress()));

      tryHideNearMeButton();
      // remember this observable - we will start listening to search below only after this fires
      Observable<DtlLocation> locationObservable = connectDtlLocationUpdate();

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
            .subscribe(aVoid -> applySearchAndOpenMerchantsPath());
      // below: treat merchant search input focus gain as location search exit (collapsing)
      getView().provideMerchantInputFocusLossObservable()
            .compose(bindView())
            .subscribe(aVoid -> applySearchAndOpenMerchantsPath());
   }

   private void applySearchAndOpenMerchantsPath() {
      filterDataInteractor.applySearch(getView().getMerchantsSearchQuery());
      navigateToPath(DtlMerchantsPath.getDefault());
   }

   private void connectToolbarLocationSearch(Observable<DtlLocation> dtlLocationObservable) {
      getView().provideLocationSearchObservable()
            .skipUntil(dtlLocationObservable)
            .debounce(250L, TimeUnit.MILLISECONDS)
            .compose(bindView())
            .subscribe(this::search);
   }

   private Observable<DtlLocation> connectDtlLocationUpdate() {
      Observable<DtlLocation> locationObservable = locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .map(LocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer());
      Observable.combineLatest(locationObservable, filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::getMerchantType), Pair::new).take(1).subscribe(pair -> {
         getView().updateToolbarTitle(pair.first, pair.second, merchantQuery);
         toolbarInitialized = true;
      });
      return locationObservable;
   }

   private void connectLocationDelegateNoFallback() {
      locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .timeout(10L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe(this::onLocationObtained, throwable -> {
               if (throwable instanceof TimeoutException) {
                  getView().hideProgress();
               }
            });
   }

   private void onLocationObtained(Location location) {
      switch (screenMode) {
         case NEARBY_LOCATIONS:
            locationInteractor.requestNearbyLocations(location);
            break;
         case AUTO_NEAR_ME:
            DtlLocation dtlLocation = ImmutableDtlLocation.builder()
                  .isExternal(false)
                  .locationSourceType(LocationSourceType.NEAR_ME)
                  .longName(context.getString(R.string.dtl_near_me_caption))
                  .coordinates(new LatLng(location.getLatitude(), location.getLongitude()))
                  .build();
            locationInteractor.changeSourceLocation(dtlLocation);
            navigateToPath(DtlMerchantsPath.withAllowedRedirection());
            break;
         case SEARCH:
            break;
         default:
            break;
      }
   }

   @Override
   public void loadNearMeRequested() {
      screenMode = ScreenMode.AUTO_NEAR_ME;

      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed()) {
         locationRequestNoFallback.unsubscribe();
      }

      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .doOnSubscribe(getView()::showProgress)
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   private void tryHideNearMeButton() {
      locationInteractor.locationSourcePipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(LocationCommand::getResult)
            .map(dtlLocation -> dtlLocation.locationSourceType())
            .filter(locationSourceType -> locationSourceType == LocationSourceType.NEAR_ME)
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> getView().hideNearMeButton());
   }

   private void connectLocationsSearch() {
      locationInteractor.searchLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SearchLocationAction>().onStart(command -> getView().showProgress())
                  .onFail((action, exception) -> {
                     getView().hideProgress();
                     apiErrorViewAdapter.handleError(action, exception);
                  })
                  .onSuccess(this::onSearchFinished));
   }

   private void onSearchFinished(SearchLocationAction action) {
      getView().setItems(action.getResult(), false);
   }

   private void mapClicked() {
      History history = History.single(new DtlMapPath(FlowUtil.currentMaster(getContext())));
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   private void navigateToPath(Path path) {
      clearCacheBeforeCloseScreen();

      History history = History.single(path);
      Flow.get(getContext()).setHistory(history, Flow.Direction.REPLACE);
   }

   private void clearCacheBeforeCloseScreen() {
      locationInteractor.searchLocationPipe().clearReplays();
      locationInteractor.nearbyLocationPipe().clearReplays();
   }

   private void search(String query) {
      screenMode = ScreenMode.SEARCH;
      locationInteractor.search(query.trim());
   }

   @Override
   public void onLocationResolutionGranted() {
      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed()) {
         locationRequestNoFallback.unsubscribe();
      }

      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
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
      if (e instanceof LocationDelegate.LocationException) {
         getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
      } else {
         onLocationResolutionDenied();
      }
   }

   private void connectNearbyLocations() {
      locationInteractor.nearbyLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<NearbyLocationAction>()
                  .onProgress((command, progress) -> getView().showProgress())
                  .onFail(this::onLocationLoadedError)
                  .onSuccess(this::onLocationsLoaded));
   }

   private void onLocationLoadedError(NearbyLocationAction action, Throwable throwable) {
      if (throwable instanceof CancelException) {
         return;
      }
      getView().informUser(action.getErrorMessage());
      getView().hideProgress();
   }

   private void onLocationsLoaded(NearbyLocationAction action) {
      getView().hideProgress();
      showLoadedLocations(action.getResult());
   }

   private void showLoadedLocations(List<DtlLocation> locations) {
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
   public void locationSelected(DtlLocation location) {
      analyticsInteractor.analyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(LocationSearchEvent.create(location)));
      locationInteractor.changeSourceLocation(location);
      applySearchAndOpenMerchantsPath();
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
