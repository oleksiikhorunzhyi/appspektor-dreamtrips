package com.worldventures.dreamtrips.wallet.service.lostcard;


import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.GetEnabledTrackingCommand;

import io.techery.janet.Command;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class LocationTrackingManager {
   private final LostCardManager lostCardManager;
   private final SmartCardLocationInteractor locationInteractor;
   private final CompositeSubscription subscriptions;

   public LocationTrackingManager(SmartCardLocationInteractor locationInteractor, LostCardManager lostCardManager) {
      this.locationInteractor = locationInteractor;
      this.lostCardManager = lostCardManager;
      this.subscriptions = new CompositeSubscription();
   }

   public void track() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeLocationTracking();
      }
      lostCardManager.connect();
   }

   public Observable<Boolean> checkEnableTracking() {
      return locationInteractor.enabledTrackingCommandActionPipe()
            .createObservableResult(new GetEnabledTrackingCommand())
            .map(Command::getResult);
   }

   private void observeLocationTracking() {
      subscriptions.add(locationInteractor.saveEnabledTrackingPipe()
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
