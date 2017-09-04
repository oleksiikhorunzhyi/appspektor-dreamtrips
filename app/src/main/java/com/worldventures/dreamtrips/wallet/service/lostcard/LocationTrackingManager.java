package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;

import io.techery.janet.Command;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class LocationTrackingManager {

   private final SmartCardInteractor smartCardInteractor;
   private final SmartCardLocationInteractor locationInteractor;
   private final LogoutInteractor logoutInteractor;
   private final WalletDetectLocationService locationService;
   private final LostCardManager lostCardManager;

   private Subscription trackSubscription;

   LocationTrackingManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor, LogoutInteractor logoutInteractor,
         WalletDetectLocationService locationService, LostCardManager lostCardManager) {
      this.smartCardInteractor = smartCardInteractor;
      this.locationInteractor = locationInteractor;
      this.logoutInteractor = logoutInteractor;
      this.locationService = locationService;
      this.lostCardManager = lostCardManager;
   }

   public void track() {
      if (trackSubscription != null && !trackSubscription.isUnsubscribed()) return;

      final Observable<Object> stopper = Observable.merge(
            smartCardInteractor.wipeSmartCardDataPipe().observeSuccess().map(command -> (Void) null),
            logoutInteractor.logoutPipe().observeSuccess().map(command -> (Void) null));

      trackSubscription = Observable.combineLatest(
            locationService.observeLocationSettingState()
                  .startWith(locationService.isEnabled()),
            locationInteractor.fetchTrackingStatusPipe().observeSuccessWithReplay().map(Command::getResult),
            locationInteractor.updateTrackingStatusPipe().observeSuccessWithReplay()
                  .doOnSubscribe(() -> locationInteractor.fetchTrackingStatusPipe().send(new FetchTrackingStatusCommand()))
                  .map(Command::getResult)
                  .startWith(true),
            (locationEnabled, updatedEnabled, fetchedEnabled) -> (locationEnabled && fetchedEnabled && updatedEnabled))
            .distinctUntilChanged()
            .takeUntil(stopper)
            .doOnUnsubscribe(() -> handleTrackingStatus(false))
            .subscribe(this::handleTrackingStatus, throwable -> Timber.e(throwable, "track"));
   }

   private void handleTrackingStatus(boolean isEnabled) {
      if (isEnabled) lostCardManager.connect();
      else lostCardManager.disconnect();
   }
}
