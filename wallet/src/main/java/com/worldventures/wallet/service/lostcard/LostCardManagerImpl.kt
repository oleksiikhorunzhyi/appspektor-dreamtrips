package com.worldventures.wallet.service.lostcard

import com.worldventures.wallet.domain.entity.ConnectionStatus
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletNetworkService
import com.worldventures.wallet.service.beacon.BeaconClient
import com.worldventures.wallet.service.beacon.RegionBundle
import com.worldventures.wallet.service.beacon.WalletBeaconLogger
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import io.techery.janet.ReadActionPipe
import io.techery.janet.smartcard.action.support.ConnectAction
import io.techery.janet.smartcard.action.support.DisconnectAction
import rx.Observable
import rx.subscriptions.CompositeSubscription
import timber.log.Timber

private const val UUID_MOTION = "92812fda-67b3-4e31-8c65-a3c6aa2bed37"
//private static final String UUID_PAYMENT = "1ff0917c-61e3-49dc-90c0-3202b6b71ec3"; //NO PMD

internal open class LostCardManagerImpl(
      private val initLocationTypeProvider: () -> Observable<WalletLocationType>,
      private val connectSCPipe: ReadActionPipe<ConnectAction>,
      private val disconnectSCPipe: ReadActionPipe<DisconnectAction>,
      private val lostCardEventReceiver: LostCardEventReceiver,
      private val locationSyncManager: LocationSyncManager,
      private val networkService: WalletNetworkService,
      private val beaconClient: BeaconClient,
      private val beaconLogger: WalletBeaconLogger) : LostCardManager {

   private val subscriptions = CompositeSubscription()

   override fun connect(smartCardId: String) {
      if (!subscriptions.hasSubscriptions() || subscriptions.isUnsubscribed) {
         beaconLogger.logBeacon("LostCardManager is connected")
         observeNetworkConnection()
         observeConnection()
         observeBeacon(smartCardId)
      }
   }

   private fun observeNetworkConnection() {
      subscriptions.add(
            networkService.observeConnectedState()
                  .startWith(networkService.isAvailable)
                  .distinctUntilChanged()
                  .subscribe { this.handleNetworkConnectivity(it) }
      )
   }

   private fun handleNetworkConnectivity(isNetworkConnected: Boolean) {
      if (isNetworkConnected) {
         locationSyncManager.scheduleSync()
      } else {
         locationSyncManager.cancelSync()
      }
   }

   private fun observeConnection() {
      subscriptions.add(
            Observable.merge(
                  connectSCPipe.observeSuccess().map { WalletLocationType.CONNECT },
                  disconnectSCPipe.observeSuccess().map { WalletLocationType.DISCONNECT }
            )
                  .startWith(initLocationTypeProvider.invoke())
                  .subscribe { triggerLocation(it) }
      )
   }

   private fun observeBeacon(smartCardId: String) {
      subscriptions.add(beaconClient.observeEvents()
            .filter { beaconEvent -> beaconEvent.smartCardId != null }
            .doOnNext { beaconEvent ->
               beaconLogger.logBeacon(
                     "Beacon ${if (beaconEvent.enteredRegion()) "detected" else "lost"} :: SmartCard ID - ${beaconEvent.smartCardId}")
            }
            .subscribe({ beaconEvent ->
               triggerLocation(if (beaconEvent.enteredRegion())
                  WalletLocationType.CONNECT
               else
                  WalletLocationType.DISCONNECT)
            }
            ) { throwable -> Timber.e(throwable, "Beacon client :: observeBeacon") }
      )

      beaconClient.startScan(RegionBundle("Motion region", UUID_MOTION, null, smartCardId))
   }

   private fun triggerLocation(locationType: WalletLocationType) {
      beaconLogger.logBeacon("SmartCard status is $locationType")
      lostCardEventReceiver.receiveEvent(locationType)
   }

   override fun disconnect() {
      beaconLogger.logBeacon("LostCardManager is disconnected")
      if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed) {
         subscriptions.clear()
      }
      beaconClient.stopScan()
      locationSyncManager.cancelSync()
   }

   companion object {

      fun createDefaultLocationTypeProvider(smartCardInteractor: SmartCardInteractor): Observable<WalletLocationType> =
            smartCardInteractor.deviceStatePipe()
                  .createObservableResult(DeviceStateCommand.fetch())
                  .filter { command -> command.result.connectionStatus === ConnectionStatus.CONNECTED }
                  .map { WalletLocationType.CONNECT }

   }
}
