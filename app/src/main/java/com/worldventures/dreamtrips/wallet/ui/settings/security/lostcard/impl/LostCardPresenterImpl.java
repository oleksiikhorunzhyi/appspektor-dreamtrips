package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl;


import com.google.android.gms.location.LocationSettingsResult;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.DisplayLocateCardAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.DisplayMapAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.LocateDisabledAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.action.LocateEnabledAnalyticsAction;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.UpdateTrackingStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.LocationScreenComponent;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardScreen;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import timber.log.Timber;

public class LostCardPresenterImpl extends WalletPresenterImpl<LostCardScreen> implements LostCardPresenter {

   private final PermissionDispatcher permissionDispatcher;
   private final SmartCardLocationInteractor smartCardLocationInteractor;
   private final WalletDetectLocationService locationService;
   private final AnalyticsInteractor analyticsInteractor;
   private final LocationScreenComponent locationScreenComponent;

   public LostCardPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, PermissionDispatcher permissionDispatcher,
         SmartCardLocationInteractor smartCardLocationInteractor, WalletDetectLocationService walletDetectLocationService,
         LocationScreenComponent locationScreenComponent, AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
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
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationUpdateTrackingStatus())
                  .onSuccess(cmd -> smartCardLocationInteractor.fetchTrackingStatusPipe().send(new FetchTrackingStatusCommand()))
                  .onFail((cmd, throwable) -> getView().revertTrackingSwitch())
                  .create());
   }

   private void observeLocationSettings() {
      locationService.observeLocationSettingState()
            .compose(bindViewIoToMainComposer())
            .subscribe(this::handleLocationSettingsStatus,
                  throwable -> Timber.e(throwable, ""));
   }

   private void observeEnableTrackingState() {
      smartCardLocationInteractor.fetchTrackingStatusPipe()
            .observeSuccess()
            .distinctUntilChanged(Command::getResult)
            .compose(bindViewIoToMainComposer())
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
               .compose(bindViewIoToMainComposer())
               .subscribe(new ActionStateSubscriber<FetchTrackingStatusCommand>()
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
            .compose(bindViewIoToMainComposer())
            .subscribe(this::checkLocationServiceResult, throwable -> Timber.d(throwable, ""));
   }

   private void checkLocationServiceResult(LocationSettingsResult result) {
      locationScreenComponent.checkSettingsResult(result)
            .compose(bindView())
            .subscribe(this::onLocationSettingsResult);
   }

   private void onLocationSettingsResult(LocationScreenComponent.EnableResult result) {
      applyTrackingStatus(result == LocationScreenComponent.EnableResult.AVAILABLE);
   }

   private void applyTrackingStatus(boolean enableTracking) {
      smartCardLocationInteractor.updateTrackingStatusPipe().send(new UpdateTrackingStatusCommand(enableTracking));
   }

   private void trackScreen() {
      smartCardLocationInteractor.fetchTrackingStatusPipe()
            .createObservable(new FetchTrackingStatusCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FetchTrackingStatusCommand>()
                  .onSuccess(command -> sendTrackScreenAction(command.getResult()))
            );
   }

   private void sendTrackScreenAction(boolean trackingEnabled) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(
                  trackingEnabled ? new DisplayMapAnalyticsAction() : new DisplayLocateCardAnalyticsAction()));
   }

   private void trackSwitchStateChanged(boolean enableTracking) {
      analyticsInteractor.locateCardAnalyticsCommandActionPipe()
            .send(new LocateCardAnalyticsCommand(enableTracking
                  ? new LocateEnabledAnalyticsAction() : new LocateDisabledAnalyticsAction()));
   }
}
