package com.worldventures.wallet.service.lostcard;

import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.service.beacon.BeaconClient;
import com.worldventures.wallet.service.beacon.RegionBundle;
import com.worldventures.wallet.service.beacon.WalletBeaconClient;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.wallet.service.lostcard.command.WalletLocationCommand;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

class LostCardManager {

   private static final String UUID_MOTION = "92812fda-67b3-4e31-8c65-a3c6aa2bed37";
   private static final String UUID_PAYMENT = "1ff0917c-61e3-49dc-90c0-3202b6b71ec3"; //NOPMD

   private final SmartCardInteractor smartCardInteractor;
   private final SmartCardLocationInteractor locationInteractor;
   private final LocationSyncManager locationSyncManager;
   private final WalletNetworkService networkService;
   private final BeaconClient beaconClient;

   private final CompositeSubscription subscriptions = new CompositeSubscription();

   LostCardManager(SmartCardInteractor smartCardInteractor, SmartCardLocationInteractor locationInteractor,
         LocationSyncManager locationSyncManager, WalletNetworkService networkService, BeaconClient beaconClient) {
      this.smartCardInteractor = smartCardInteractor;
      this.locationInteractor = locationInteractor;
      this.locationSyncManager = locationSyncManager;
      this.networkService = networkService;
      this.beaconClient = beaconClient;
   }

   public void connect() {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed()) {
         observeNetworkConnection();
         observeConnection();
         observeBeacon();
      }
      if (networkService.isAvailable()) {
         locationSyncManager.scheduleSync();
      }
   }

   private void observeNetworkConnection() {
      subscriptions.add(networkService.observeConnectedState()
            .subscribe(this::handleNetworkConnectivity));
   }

   private void handleNetworkConnectivity(boolean isNetworkConnected) {
      if (isNetworkConnected) {
         locationSyncManager.scheduleSync();
      } else {
         locationSyncManager.cancelSync();
      }
   }

   private void observeConnection() {
      subscriptions.add(smartCardInteractor.deviceStatePipe()
            .createObservableResult(DeviceStateCommand.fetch())
            .filter(command -> command.getResult().connectionStatus() == ConnectionStatus.CONNECTED)
            .subscribe(command -> triggerLocation(WalletLocationType.CONNECT), throwable -> { /* ignore */ }));

      subscriptions.add(locationInteractor.connectActionPipe()
            .observeSuccess()
            .doOnNext(connectAction -> WalletBeaconClient.logBeacon("SmartCard connected"))
            .subscribe(connectAction -> triggerLocation(WalletLocationType.CONNECT)));

      subscriptions.add(locationInteractor.disconnectPipe()
            .observeSuccess()
            .doOnNext(connectAction -> WalletBeaconClient.logBeacon("SmartCard disconnected"))
            .subscribe(disconnectAction -> triggerLocation(WalletLocationType.DISCONNECT)));
   }

   private void observeBeacon() {
      subscriptions.add(smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new ActiveSmartCardCommand())
            .map(command -> command.getResult().smartCardId())
            .flatMap(activeSmartCardId -> beaconClient.observeEvents()
                  .filter(beaconEvent -> beaconEvent.getSmartCardId() != null)
                  .doOnNext(beaconEvent -> WalletBeaconClient.logBeacon("Beacon %s :: SmartCard ID - %s",
                        beaconEvent.enteredRegion() ? "detected" : "lost", beaconEvent.getSmartCardId()))
                  .doOnSubscribe(() -> beaconClient.startScan(
                        new RegionBundle("Motion region", UUID_MOTION, null, activeSmartCardId)))
                  .doOnUnsubscribe(beaconClient::stopScan))

            .subscribe(beaconEvent -> triggerLocation(beaconEvent.enteredRegion()
                        ? WalletLocationType.CONNECT : WalletLocationType.DISCONNECT),
                  throwable -> Timber.e(throwable, "Beacon client :: observeBeacon"))
      );
   }

   private void triggerLocation(WalletLocationType locationType) {
      locationInteractor.walletLocationCommandPipe().send(new WalletLocationCommand(locationType));
   }

   public void disconnect() {
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
         subscriptions.clear();
      }
      locationSyncManager.cancelSync();
   }
}
