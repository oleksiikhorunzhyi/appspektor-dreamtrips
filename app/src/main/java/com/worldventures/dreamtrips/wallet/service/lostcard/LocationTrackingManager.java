package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
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
   private final LoginInteractor loginInteractor;
   private final WalletDetectLocationService locationService;
   private final LostCardManager lostCardManager;
   private final SessionHolder sessionHolder;

   private Subscription trackSubscription;
   private Subscription reloginSubscription;

   LocationTrackingManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LogoutInteractor logoutInteractor, WalletDetectLocationService locationService, LostCardManager lostCardManager,
         LoginInteractor loginInteractor, SessionHolder sessionHolder) {
      this.smartCardInteractor = smartCardInteractor;
      this.locationInteractor = locationInteractor;
      this.logoutInteractor = logoutInteractor;
      this.locationService = locationService;
      this.lostCardManager = lostCardManager;
      this.loginInteractor = loginInteractor;
      this.sessionHolder = sessionHolder;
   }

   public void track() {
      if (trackSubscription != null && !trackSubscription.isUnsubscribed()) return;
      if (reloginSubscription != null && !reloginSubscription.isUnsubscribed()) reloginSubscription.unsubscribe();

      final Observable<Object> stopper = Observable.merge(
            smartCardInteractor.wipeSmartCardDataPipe().observeSuccess().map(command -> (Void) null),
            logoutInteractor.logoutPipe().observeSuccess().map(command -> (Void) null));

      trackSubscription = Observable.combineLatest(
            locationService.observeLocationSettingState().startWith(locationService.isEnabled()),
            loginInteractor.loginActionPipe().observeSuccessWithReplay()
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
      reloginSubscription = loginInteractor.loginActionPipe()
            .observeSuccessWithReplay()
            .map(Command::getResult)
            .subscribe(session -> track());
   }

   private void handleTrackingStatus(boolean isEnabled) {
      if (isEnabled) lostCardManager.connect();
      else lostCardManager.disconnect();
   }
}
