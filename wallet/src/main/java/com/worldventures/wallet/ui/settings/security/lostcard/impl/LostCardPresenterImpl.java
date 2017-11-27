package com.worldventures.wallet.ui.settings.security.lostcard.impl;

import com.google.android.gms.location.LocationSettingsResult;
import com.worldventures.core.ui.util.permission.PermissionConstants;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.ui.util.permission.PermissionSubscriber;
import com.worldventures.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.wallet.analytics.locatecard.action.DisplayLocateCardAnalyticsAction;
import com.worldventures.wallet.analytics.locatecard.action.DisplayMapAnalyticsAction;
import com.worldventures.wallet.analytics.locatecard.action.LocateDisabledAnalyticsAction;
import com.worldventures.wallet.analytics.locatecard.action.LocateEnabledAnalyticsAction;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.location.WalletDetectLocationService;
import com.worldventures.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.wallet.ui.common.LocationScreenComponent;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardScreen;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class LostCardPresenterImpl extends WalletPresenterImpl<LostCardScreen> implements LostCardPresenter {

   private final PermissionDispatcher permissionDispatcher;
   private final SmartCardLocationInteractor smartCardLocationInteractor;
   private final WalletDetectLocationService locationService;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final LocationScreenComponent locationScreenComponent;

   public LostCardPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         PermissionDispatcher permissionDispatcher,
         SmartCardLocationInteractor smartCardLocationInteractor, WalletDetectLocationService walletDetectLocationService,
         LocationScreenComponent locationScreenComponent, WalletAnalyticsInteractor analyticsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.permissionDispatcher = permissionDispatcher;
      this.smartCardLocationInteractor = smartCardLocationInteractor;
      this.locationService = walletDetectLocationService;
      this.locationScreenComponent = locationScreenComponent;
      this.analyticsInteractor = analyticsInteractor;
   }

   @Override
   public void attachView(LostCardScreen view) {
      super.attachView(view);
      trackScreen();

      observeCheckingSwitcher();
      observeUpdateTrackingStatus();
   }

   private void observeUpdateTrackingStatus() {
      smartCardLocationInteractor.updateTrackingStatusPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationUpdateTrackingStatus())
                  .onSuccess(cmd -> onTrackingStateFetched(cmd.getResult()))
                  .onFail((cmd, throwable) -> getView().revertTrackingSwitch())
                  .create());
   }

   private void observeLocationSettings() {
      locationService.observeLocationSettingState()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleLocationSettingsStatus, Timber::e);
   }

   private void observeEnableTrackingState() {
      smartCardLocationInteractor.fetchTrackingStatusPipe()
            .observeSuccess()
            .distinctUntilChanged(Command::getResult)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> onTrackingStateFetched(command.getResult()));
   }

   @Override
   public void prepareTrackingStateSubscriptions() {
      observeEnableTrackingState();
      observeLocationSettings();

      smartCardLocationInteractor.fetchTrackingStatusPipe().send(new FetchTrackingStatusCommand());
   }

   private void handleLocationSettingsStatus(boolean isEnabled) {
      if (!isEnabled) {
         smartCardLocationInteractor.fetchTrackingStatusPipe()
               .createObservable(new FetchTrackingStatusCommand())
               .compose(getView().bindUntilDetach())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ActionStateSubscriber<FetchTrackingStatusCommand>()
                     .onSuccess(command -> onTrackingStateFetched(command.getResult())));
      }
   }

   private void onTrackingStateFetched(boolean state) {
      if (state && (!locationService.isEnabled() || !locationService.isPermissionGranted())) {
         requestLocationPermissions(true);
      }
      applyTrackingStatusForUI(state);
   }

   private void observeCheckingSwitcher() {
      getView().observeTrackingEnable()
            .compose(getView().bindUntilDetach())
            .subscribe(this::onTrackingSwitcherChanged);
   }

   private void onTrackingSwitcherChanged(boolean enableTracking) {
      getView().switcherEnable(false);
      trackSwitchStateChanged(enableTracking);
      if (enableTracking) {
         applyTrackingStatus(true);
      } else {
         getView().showDisableConfirmationDialog();
      }
   }

   private void requestLocationPermissions(boolean showRationale) {
      permissionDispatcher.requestPermission(PermissionConstants.LOCATION_PERMISSIONS, showRationale)
            .compose(getView().bindUntilDetach())
            .subscribe(new PermissionSubscriber()
                  .onPermissionGrantedAction(this::checkLocationSettings)
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
      getView().switcherEnable(true);
      getView().setTrackingSwitchStatus(isTrackingEnabled);
      getView().setMapEnabled(isTrackingEnabled);
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

   private void checkLocationSettings() {
      locationService.fetchLastKnownLocationSettings()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::checkLocationServiceResult, Timber::e);
   }

   private void checkLocationServiceResult(LocationSettingsResult result) {
      locationScreenComponent.checkSettingsResult(result)
            .compose(getView().bindUntilDetach())
            .subscribe(this::onLocationSettingsResult);
   }

   private void onLocationSettingsResult(LocationScreenComponent.EnableResult result) {
      applyTrackingStatus(result == LocationScreenComponent.EnableResult.AVAILABLE);
   }

   private void applyTrackingStatus(boolean enableTracking) {
      getView().switcherEnable(true);
      smartCardLocationInteractor.updateTrackingStatusPipe().send(new UpdateTrackingStatusCommand(enableTracking));
   }

   private void trackScreen() {
      smartCardLocationInteractor.fetchTrackingStatusPipe()
            .createObservable(new FetchTrackingStatusCommand())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<FetchTrackingStatusCommand>()
                  .onSuccess(command -> sendTrackScreenAction(command.getResult()))
            );
   }

   private void sendTrackScreenAction(boolean trackingEnabled) {
      analyticsInteractor.locateCardAnalyticsPipe()
            .send(new LocateCardAnalyticsCommand(
                  trackingEnabled ? new DisplayMapAnalyticsAction() : new DisplayLocateCardAnalyticsAction()));
   }

   private void trackSwitchStateChanged(boolean enableTracking) {
      analyticsInteractor.locateCardAnalyticsPipe()
            .send(new LocateCardAnalyticsCommand(enableTracking
                  ? new LocateEnabledAnalyticsAction() : new LocateDisabledAnalyticsAction()));
   }
}
