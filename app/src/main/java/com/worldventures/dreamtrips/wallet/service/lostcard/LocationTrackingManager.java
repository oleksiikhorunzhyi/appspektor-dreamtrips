package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.CardTrackingStatusCommand;

import io.techery.janet.Command;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class LocationTrackingManager {
   private final LostCardManager lostCardManager;
   private final SmartCardLocationInteractor locationInteractor;
   private final WalletDetectLocationService locationService;
   private final CompositeSubscription subscriptions;

   public LocationTrackingManager(SmartCardLocationInteractor locationInteractor,
         WalletDetectLocationService locationService, LostCardManager lostCardManager) {
      this.locationInteractor = locationInteractor;
      this.locationService = locationService;
      this.lostCardManager = lostCardManager;
      this.subscriptions = new CompositeSubscription();
   }

   public void track() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeLocationSettings();
         observeLocationTracking();
      }
      checkEnableTracking()
            .subscribe(this::handleTrackingStatus, throwable -> Timber.e(throwable, ""));
   }

   private Observable<Boolean> checkEnableTracking() {
      return Observable.zip(Observable.just(locationService.isEnabled()),
            locationInteractor.enabledTrackingPipe()
            .createObservableResult(CardTrackingStatusCommand.fetch())
            .map(Command::getResult),
            (locationSettingsEnabled, trackingStatusEnabled) -> locationSettingsEnabled && trackingStatusEnabled
      );
   }

   private void observeLocationSettings() {
      subscriptions.add(locationService.observeLocationSettingState()
            .subscribe(this::handleTrackingStatus));
   }

   private void observeLocationTracking() {
      subscriptions.add(locationInteractor.enabledTrackingPipe()
            .observeSuccess()
            .map(Command::getResult)
            .subscribe(this::handleTrackingStatus));
   }

   private void handleTrackingStatus(boolean isEnabled) {
      if (isEnabled) {
         lostCardManager.connect();
      } else {
         lostCardManager.disconnect();
      }
   }

   public void untrack() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
      lostCardManager.disconnect();
   }
}
