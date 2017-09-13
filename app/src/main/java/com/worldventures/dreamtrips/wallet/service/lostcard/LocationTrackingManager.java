package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
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
   private final AuthInteractor authInteractor;
   private final WalletDetectLocationService locationService;
   private final LostCardManager lostCardManager;
   private final SessionHolder sessionHolder;

   private Subscription trackSubscription;
   private Subscription reloginSubscription;

   LocationTrackingManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         WalletDetectLocationService locationService, AuthInteractor authInteractor, LostCardManager lostCardManager,
         SessionHolder sessionHolder) {
      this.smartCardInteractor = smartCardInteractor;
      this.locationInteractor = locationInteractor;
      this.locationService = locationService;
      this.authInteractor = authInteractor;
      this.lostCardManager = lostCardManager;
      this.sessionHolder = sessionHolder;
   }

   public void track() {
      if (trackSubscription != null && !trackSubscription.isUnsubscribed()) return;
      if (reloginSubscription != null && !reloginSubscription.isUnsubscribed()) reloginSubscription.unsubscribe();

      final Observable<Object> stopper = Observable.merge(
            smartCardInteractor.wipeSmartCardDataPipe().observeSuccess().map(command -> (Void) null),
            authInteractor.logoutPipe().observeSuccess().map(command -> (Void) null));

      trackSubscription = Observable.combineLatest(
            locationService.observeLocationSettingState().startWith(locationService.isEnabled()),
            authInteractor.loginActionPipe().observeSuccessWithReplay()
                  .map(Command::getResult).startWith(sessionHolder.get().orNull()),
            locationInteractor.updateTrackingStatusPipe().observeSuccessWithReplay()
                  .map(Command::getResult)
                  .startWith(locationInteractor.fetchTrackingStatusPipe()
                        .createObservableResult(new FetchTrackingStatusCommand())
                        .map(Command::getResult)),
            (locationEnabled, userSession, updatedEnabled) ->
                  (userSession != null && locationEnabled && updatedEnabled))
            .distinctUntilChanged()
            .takeUntil(stopper)
            .doOnUnsubscribe(() -> {
               handleTrackingStatus(false);
               observeRelogin();
            }).subscribe(this::handleTrackingStatus, throwable -> Timber.e(throwable, "track"));
   }

   private void observeRelogin() {
      reloginSubscription = authInteractor.loginActionPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .subscribe(session -> track());
   }

   private void handleTrackingStatus(boolean isEnabled) {
      if (isEnabled) lostCardManager.connect();
      else lostCardManager.disconnect();
   }
}
