package com.worldventures.wallet.service.lostcard.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation
import com.worldventures.wallet.domain.entity.lostcard.WalletLocationType
import com.worldventures.wallet.service.SmartCardLocationInteractor
import com.worldventures.wallet.service.beacon.WalletBeaconLogger
import com.worldventures.wallet.service.lostcard.LostCardRepository
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import java.util.Calendar
import javax.inject.Inject

@CommandAction
class WalletLocationCommand(private val locationType: WalletLocationType) : Command<WalletLocation>(), InjectableAction {

   @Inject lateinit var locationInteractor: SmartCardLocationInteractor
   @Inject lateinit var locationRepository: LostCardRepository
   @Inject lateinit var walletBeaconLogger: WalletBeaconLogger

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<WalletLocation>) {
      observeLocationDetection()
            .map { WalletLocation(createdAt = Calendar.getInstance().time, type = locationType, coordinates = it.result) }
            .flatMap { this.saveLocation(it) }
            .doOnError { throwable -> walletBeaconLogger.logBeacon("WalletLocationCommand error - %s", throwable.message) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun observeLocationDetection(): Observable<DetectGeoLocationCommand> = locationInteractor.detectGeoLocationPipe()
         .createObservableResult(DetectGeoLocationCommand())

   private fun saveLocation(location: WalletLocation): Observable<WalletLocation> {
      walletBeaconLogger.logBeacon("Save location - %s", location)
      val walletLocations = locationRepository.walletLocations
      walletLocations.add(location)
      locationRepository.saveWalletLocations(walletLocations)
      return Observable.just(location)
   }

}
