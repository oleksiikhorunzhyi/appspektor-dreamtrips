package com.worldventures.dreamtrips.modules.dtl_flow.parts.master_toolbar;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.LocationSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.NearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SearchLocationAction;
import com.worldventures.dreamtrips.modules.dtl.view.util.MerchantTypeUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.location_change.DtlLocationChangePresenterImpl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.CancelException;
import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MasterToolbarPresenterImpl extends DtlPresenterImpl<MasterToolbarScreen, MasterToolbarState> implements MasterToolbarPresenter {

   @Inject DtlLocationInteractor locationInteractor;
   @Inject FilterDataInteractor filterDataInteractor;
   @Inject LocationDelegate gpsLocationDelegate;
   @Inject MerchantsInteractor merchantInteractor;

   @State DtlLocationChangePresenterImpl.ScreenMode screenMode = DtlLocationChangePresenterImpl.ScreenMode.NEARBY_LOCATIONS;

   private AtomicBoolean showAutodetectButton = new AtomicBoolean(Boolean.TRUE);
   private AtomicBoolean noMerchants = new AtomicBoolean(Boolean.FALSE);

   private Subscription locationRequestNoFallback;

   public MasterToolbarPresenterImpl(Context context, Injector injector) {
      super(context);
      injector.inject(this);
   }

   @Override
   public void onNewViewState() {
      state = new MasterToolbarState();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setPopupShowing(getView().isSearchPopupShowing());
      state.setScreenMode(screenMode);
      super.onSaveInstanceState(bundle);
   }

   @Override
   public void onRestoreInstanceState(Bundle instanceState) {
      super.onRestoreInstanceState(instanceState);
      if (state.isPopupShowing()) getView().toggleSearchPopupVisibility(true);
      this.screenMode = state.getScreenMode();
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      apiErrorPresenter.setView(getView());

      updateToolbarTitles();

      bindToolbarLocationCaptionUpdates();
      connectFilterDataChanges();

      tryHideNearMeButton();

      connectNearbyLocations();
      connectLocationsSearch();
      connectMerchants();
      connectLocationDelegateNoFallback();
      connectToolbarLocationSearchInput();

      connectFilterToggle();
   }

   private void connectFilterToggle() {
      filterDataInteractor.filterDataPipe()
            .observe()
            .map(filterDataActionActionState -> filterDataActionActionState)
            .subscribe(filterDataActionActionState -> filterDataInteractor.filterDataPipe()
                        .observeSuccessWithReplay()
                        .take(1)
                        .map(this::getMerchantType)
                        .compose(bindViewIoToMainComposer())
                        .subscribe(this::updateToolbarTitle, throwable -> {
                        }),
                  throwable -> {
                  });
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::isOffersOnly)
            .doOnCompleted(getView()::connectToggleUpdate)
            .subscribe(getView()::toggleOffersOnly);
   }

   private String getMerchantType(FilterDataAction filterDataAction) {
      return filterDataAction
            .getResult()
            .getMerchantType()
            .get(filterDataAction.getResult()
                  .getMerchantType()
                  .size() - 1);
   }

   private void updateToolbarTitle(String type) {
      getView().updateToolbarSearchCaption(getContext().getString(MerchantTypeUtil
            .getStringResource(type)));
   }

   private void connectFilterDataChanges() {
      filterDataInteractor.filterDataPipe()
            .observeSuccess()
            .map(FilterDataAction::getResult)
            .compose(bindViewIoToMainComposer())
            .map(FilterData::isDefault)
            .subscribe(getView()::setFilterButtonState);
   }

   private void connectMerchants() {
      merchantInteractor.thinMerchantsHttpPipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .map(List::isEmpty)
            .subscribe(noMerchants::set);
   }

   @Override
   public void offersOnlySwitched(boolean isOffersOnly) {
      filterDataInteractor.applyOffersOnly(isOffersOnly);
   }

   @Override
   public void applySearch(String query) {
      filterDataInteractor.applySearch(query);
   }

   private void connectToolbarLocationSearchInput() {
      getView().provideLocationSearchObservable()
            .filter(dtlLocationCommand -> getView().isSearchPopupShowing())
            .debounce(250L, TimeUnit.MILLISECONDS)
            .compose(bindView())
            .subscribe(this::locationSearch);
   }

   private void updateToolbarTitles() {
      locationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(LocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .compose(bindViewIoToMainComposer())
            .map(FilterDataAction::getResult)
            .map(FilterData::searchQuery)
            .subscribe(getView()::updateToolbarSearchCaption);
   }

   private void bindToolbarLocationCaptionUpdates() {
      locationInteractor.locationFacadePipe()
            .observeSuccess()
            .filter(dtlLocationCommand -> !getView().isSearchPopupShowing())
            .map(LocationFacadeCommand::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(getView()::updateToolbarLocationTitle);
   }

   private void connectLocationDelegateNoFallback() {
      locationRequestNoFallback = gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .timeout(10L, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .subscribe(this::onLocationObtained, throwable -> {
               getView().hideProgress();
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
            break;
      }
   }

   @Override
   public void loadNearMeRequested() {
      screenMode = DtlLocationChangePresenterImpl.ScreenMode.AUTO_NEAR_ME;

      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed())
         locationRequestNoFallback.unsubscribe();

      gpsLocationDelegate.requestLocationUpdate()
            .compose(bindViewIoToMainComposer())
            .doOnSubscribe(getView()::showProgress)
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   private void tryHideNearMeButton() {
      locationInteractor.locationSourcePipe()
            .observeSuccessWithReplay()
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult)
            .map(DtlLocation::locationSourceType)
            .map(mode -> mode != LocationSourceType.NEAR_ME)
            .subscribe(showAutodetectButton::set);
   }

   private void connectLocationsSearch() {
      locationInteractor.searchLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SearchLocationAction>()
                  .onStart(command -> getView().showProgress())
                  .onFail(apiErrorPresenter::handleActionError)
                  .onSuccess(this::onSearchFinished));
   }

   private void onSearchFinished(SearchLocationAction action) {
      getView().setItems(action.getResult(), false);
   }

   private void locationSearch(String query) {
      screenMode = DtlLocationChangePresenterImpl.ScreenMode.SEARCH;
      locationInteractor.search(query.trim());
   }

   @Override
   public void onLocationResolutionGranted() {
      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed()) {
         locationRequestNoFallback.unsubscribe();
      }

      gpsLocationDelegate.getLastKnownLocation()
            .compose(new IoToMainComposer<>())
            .subscribe(this::onLocationObtained, this::onLocationError);
   }

   @Override
   public void onLocationResolutionDenied() {
      getView().hideProgress();
   }

   @Override
   public void onShowToolbar() {
      if (locationRequestNoFallback != null && !locationRequestNoFallback.isUnsubscribed()) {
         locationRequestNoFallback.unsubscribe();
      }

      gpsLocationDelegate.requestLocationUpdate()
            .compose(new IoToMainComposer<>())
            .subscribe(locationInteractor::requestNearbyLocations, throwable -> getView().hideProgress());
   }

   @Override
   public boolean needShowAutodetectButton() {
      return showAutodetectButton.get();
   }

   private void onLocationError(Throwable e) {
      if (e instanceof LocationDelegate.LocationException)
         getView().locationResolutionRequired(((LocationDelegate.LocationException) e).getStatus());
      else onLocationResolutionDenied();
   }

   private void connectNearbyLocations() {
      locationInteractor.nearbyLocationPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<NearbyLocationAction>()
                  .onProgress((action, progress) -> getView().showProgress())
                  .onFail(this::onLocationLoadedError)
                  .onSuccess(this::onLocationsLoaded));
   }

   private void onLocationLoadedError(NearbyLocationAction action, Throwable throwable) {
      if (throwable instanceof CancelException) return;
      getView().informUser(action.getErrorMessage());
      getView().hideProgress();
   }

   private void onLocationsLoaded(NearbyLocationAction action) {
      getView().hideProgress();
      getView().setItems(action.getResult(), !action.getResult().isEmpty());
   }

   @Override
   public void locationSelected(DtlLocation dtlExternalLocation) {
      analyticsInteractor.dtlAnalyticsCommandPipe()
            .send(DtlAnalyticsCommand.create(LocationSearchEvent.create(dtlExternalLocation)));
      locationInteractor.changeSourceLocation(dtlExternalLocation);
   }
}
