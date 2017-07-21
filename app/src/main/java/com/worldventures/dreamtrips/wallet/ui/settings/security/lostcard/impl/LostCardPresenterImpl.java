package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.ClickDirectionsAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.DisplayLocateCardAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.DisplayMapAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.LocateDisabledAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.LocateEnabledAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletAddress;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletPlace;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.location.LocationSettingsService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.CardTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchAddressWithPlacesCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetLocationCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.model.ImmutableLostCardPin;

import java.util.List;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.getLatestLocation;
import static com.worldventures.dreamtrips.wallet.util.WalletLocationsUtil.toLatLng;

public class LostCardPresenterImpl extends WalletPresenterImpl<LostCardScreen> implements LostCardPresenter {

   private final PermissionDispatcher permissionDispatcher;
   private final SmartCardLocationInteractor smartCardLocationInteractor;
   private final WalletDetectLocationService locationService;
   private final AnalyticsInteractor analyticsInteractor;
   private final HttpErrorHandlingUtil httpErrorHandlingUtil;

   public LostCardPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, PermissionDispatcher permissionDispatcher,
         SmartCardLocationInteractor smartCardLocationInteractor, WalletDetectLocationService walletDetectLocationService,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      super(navigator, smartCardInteractor, networkService);
      this.permissionDispatcher = permissionDispatcher;
      this.smartCardLocationInteractor = smartCardLocationInteractor;
      this.locationService = walletDetectLocationService;
      this.analyticsInteractor = analyticsInteractor;
      this.httpErrorHandlingUtil = httpErrorHandlingUtil;
   }

   @Override
   public void attachView(LostCardScreen view) {
      super.attachView(view);
      trackScreen();

      observeCheckingSwitcher();
      observeWalletLocationCommand();
      observeEnableTrackingState();
      observeLocationSettings();

      fetchEnableTrackingState();
   }

   private void observeLocationSettings() {
      locationService.observeLocationSettingState()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleLocationSettingsStatus,
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeWalletLocationCommand() {
      smartCardLocationInteractor.walletLocationCommandPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(walletLocationCommand -> processLastLocation(walletLocationCommand.getResult()),
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingPipe()
            .observeSuccess()
            .distinctUntilChanged(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> onTrackingStateFetched(command.getResult()));
   }

   private void fetchEnableTrackingState() {
      smartCardLocationInteractor.enabledTrackingPipe().send(CardTrackingStatusCommand.fetch());
   }

   private void handleLocationSettingsStatus(boolean isEnabled) {
      if (!isEnabled) {
         smartCardLocationInteractor.enabledTrackingPipe()
               .createObservable(CardTrackingStatusCommand.fetch())
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<CardTrackingStatusCommand>()
                     .onSuccess(command -> onTrackingStateFetched(command.getResult())));
      }
   }

   private void onTrackingStateFetched(boolean state) {
      if (state) {
         requestLocationPermissions(true);
      }
      applyTrackingStatusForUI(state);
   }

   private void observeCheckingSwitcher() {
      getView().observeTrackingEnable()
            .compose(bindView())
            .subscribe(this::onTrackingSwitcherChanged);
   }

   private void onTrackingSwitcherChanged(boolean enableTracking) {
      trackSwitchStateChanged(enableTracking);
      if (enableTracking) {
         applyTrackingStatus(true);
      } else {
         getView().showDisableConfirmationDialog();
      }
   }

   private void requestLocationPermissions(boolean showRationale) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS, showRationale)
            .compose(bindView())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(this::checkLocationServiceEnabled)
                  .onPermissionRationaleAction(() -> {
                     getView().showRationaleForLocation();
                     applyTrackingStatus(false);
                  })
                  .onPermissionDeniedAction(() -> {
                     getView().showDeniedForLocation();
                     applyTrackingStatus(false);
                  }));
   }

   private void applyTrackingStatusForUI(boolean isTrackingEnabled) {
      getView().setTrackingSwitchStatus(isTrackingEnabled);

      getView().setVisibleDisabledTrackingView(!isTrackingEnabled);
      getView().setVisibilityMap(isTrackingEnabled);
   }

   @Override
   public void onMapPrepared() {
      fetchLastSmartCardLocation();
   }

   @Override
   public void disableTracking() {
      applyTrackingStatus(false);
   }

   @Override
   public void onPermissionRationaleClick() {
      requestLocationPermissions(false);
   }

   @Override
   public void disableTrackingCanceled() {
      applyTrackingStatusForUI(true);
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void retryFetchAddressWithPlaces(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      fetchAddressWithPlaces(fetchAddressWithPlacesCommand.getCoordinates());
   }

   private void checkLocationServiceEnabled() {
      getView().getLocationSettingsService().enableLocationApi()
            .compose(bindView())
            .take(1)
            .subscribe(this::onLocationSettingsResult);
   }

   private void onLocationSettingsResult(LocationSettingsService.EnableResult result) {
      applyTrackingStatus(result == LocationSettingsService.EnableResult.AVAILABLE);
   }

   private void applyTrackingStatus(boolean enableTracking) {
      smartCardLocationInteractor.enabledTrackingPipe().send(CardTrackingStatusCommand.save(enableTracking));
   }

   private void fetchLastSmartCardLocation() {
      smartCardLocationInteractor.getLocationPipe()
            .createObservable(new GetLocationCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<GetLocationCommand>()
                  .onSuccess(getLocationCommand -> {
                     final WalletLocation walletLocation = getLatestLocation(getLocationCommand.getResult());
                     processLastLocation(walletLocation);
                  })
            );
   }

   private void processLastLocation(WalletLocation walletLocation) {
      if (walletLocation == null) {
         toggleLocationContainersVisibility(false);
         return;
      }
      toggleLocationContainersVisibility(true);
      getView().setLastConnectionDate(walletLocation.createdAt());

      fetchAddressWithPlaces(walletLocation.coordinates());
   }

   private void fetchAddressWithPlaces(WalletCoordinates coordinates) {
      smartCardLocationInteractor.fetchAddressPipe()
            .createObservable(new FetchAddressWithPlacesCommand(coordinates))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView(), true)
                  .onSuccess(command ->
                        setupLocationAndAddress(coordinates, command.getResult().address, command.getResult().places))
                  .onFail((fetchAddressWithPlacesCommand, throwable) -> setupEmptyLocation(fetchAddressWithPlacesCommand))
                  .create());
   }

   private void setupEmptyLocation(FetchAddressWithPlacesCommand fetchAddressWithPlacesCommand) {
      getView().addPin(toLatLng(fetchAddressWithPlacesCommand.getCoordinates()));
   }

   private void toggleLocationContainersVisibility(boolean locationExists) {
      getView().setVisibleMsgEmptyLastLocation(!locationExists);
      getView().setVisibleLastConnectionTime(locationExists);
   }

   private void setupLocationAndAddress(WalletCoordinates coordinates, WalletAddress address, List<WalletPlace> places) {
      getView().addPin(ImmutableLostCardPin.builder()
            .position(toLatLng(coordinates))
            .address(address)
            .places(places)
            .build());
   }

   private void trackScreen() {
      smartCardLocationInteractor.enabledTrackingPipe()
            .createObservable(CardTrackingStatusCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<CardTrackingStatusCommand>()
                  .onSuccess(command -> sendTrackScreenAction(command.getResult()))
            );
   }

   private void sendTrackScreenAction(boolean trackingEnabled) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(
                  trackingEnabled ? new DisplayMapAnalyticsAction() : new DisplayLocateCardAnalyticsAction()));
   }

   @Override
   public void trackDirectionsClick() {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(new ClickDirectionsAnalyticsAction()));
   }

   private void trackSwitchStateChanged(boolean enableTracking) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(enableTracking
                  ? new LocateEnabledAnalyticsAction() : new LocateDisabledAnalyticsAction()));
   }

   public HttpErrorHandlingUtil httpErrorHandlingUtil() {
      return httpErrorHandlingUtil;
   }
}
