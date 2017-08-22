package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.service.LogoutInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.FetchTrackingStatusCommand;

import io.techery.janet.Command;
import rx.Observable;
import timber.log.Timber;

public class LocationTrackingManager {
   private final LostCardManager lostCardManager;
   private final SmartCardLocationInteractor locationInteractor;
   private final SmartCardInteractor smartCardInteractor;
   private final LoginInteractor loginInteractor;
   private final LogoutInteractor logoutInteractor;
   private final WalletSocialInfoProvider walletSocialInfoProvider;

   public LocationTrackingManager(SmartCardLocationInteractor locationInteractor, LostCardManager lostCardManager,
         SmartCardInteractor smartCardInteractor, LoginInteractor loginInteractor,
         LogoutInteractor logoutInteractor, WalletSocialInfoProvider walletSocialInfoProvider) {
      this.locationInteractor = locationInteractor;
      this.lostCardManager = lostCardManager;
      this.smartCardInteractor = smartCardInteractor;
      this.loginInteractor = loginInteractor;
      this.logoutInteractor = logoutInteractor;
      this.walletSocialInfoProvider = walletSocialInfoProvider;
   }

   public void init() {
      loginInteractor.loginActionPipe().observeSuccess().map(command -> true)
            .startWith(Observable.just(walletSocialInfoProvider.hasUser()))
            .flatMap(isUserPresent -> isUserPresent ? checkSmartCardExistence() : Observable.just(false))
            .flatMap(smartCardExists -> smartCardExists ? checkEnableTracking() : Observable.empty())
            .mergeWith(locationInteractor.updateTrackingStatusPipe().observeSuccess().map(Command::getResult))
            .mergeWith(logoutInteractor.logoutPipe().observeSuccess().map(command -> false))
            .distinctUntilChanged()
            .subscribe(this::handleTrackingStatus, throwable -> Timber.e(throwable, ""));
   }

   private Observable<Boolean> checkEnableTracking() {
      return locationInteractor.fetchTrackingStatusPipe()
            .createObservableResult(new FetchTrackingStatusCommand())
            .map(Command::getResult);
   }

   private Observable<Boolean> checkSmartCardExistence() {
      return smartCardInteractor.fetchAssociatedSmartCard()
            .createObservableResult(new FetchAssociatedSmartCardCommand())
            .map(command -> command.getResult().exist());
   }

   private void handleTrackingStatus(boolean isEnabled) {
      if (isEnabled) {
         lostCardManager.connect();
      } else {
         lostCardManager.disconnect();
      }
   }
}
