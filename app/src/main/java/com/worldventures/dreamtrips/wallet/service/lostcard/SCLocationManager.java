package com.worldventures.dreamtrips.wallet.service.lostcard;

import com.worldventures.dreamtrips.api.smart_card.location.model.SmartCardLocationType;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;

import java.util.concurrent.TimeUnit;

import rx.subscriptions.CompositeSubscription;

public class SCLocationManager {

   private final SmartCardLocationInteractor locationInteractor;
   private final CompositeSubscription subscriptions;

   public SCLocationManager(SmartCardLocationInteractor locationInteractor) {
      this.locationInteractor = locationInteractor;
      this.subscriptions = new CompositeSubscription();
   }

   public void connect() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeConnection();
      }
   }

   private void observeConnection() {
      subscriptions.add(locationInteractor.connectActionPipe()
            .observeSuccess()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe(connectAction ->  triggerLocation(SmartCardLocationType.CONNECT)));

      subscriptions.add(locationInteractor.disconnectPipe()
            .observeSuccess()
            .subscribe(disconnectAction ->  triggerLocation(SmartCardLocationType.DISCONNECT)));
   }

   private void triggerLocation(SmartCardLocationType locationType) {
      locationInteractor.detectGeoLocationPipe().send(new DetectGeoLocationCommand(locationType));
   }

   public void disconnect() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
   }
}
