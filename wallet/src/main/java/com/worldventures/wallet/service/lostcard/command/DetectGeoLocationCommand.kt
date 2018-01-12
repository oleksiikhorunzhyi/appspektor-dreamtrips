package com.worldventures.wallet.service.lostcard.command

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.core.service.location.DetectLocationService
import com.worldventures.wallet.service.lostcard.LostCardRepository
import com.worldventures.wallet.util.LocationPermissionDeniedException
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class DetectGeoLocationCommand : Command<WalletCoordinates>(), InjectableAction {

   @Inject lateinit var locationService: DetectLocationService
   @Inject lateinit var locationRepository: LostCardRepository

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<WalletCoordinates>) {
      if (!locationService.isPermissionGranted) {
         throw LocationPermissionDeniedException()
      }
      locationService.detectLastKnownLocation()
            .flatMap { location ->
               return@flatMap if (location == null) {
                  Observable.error(IllegalStateException("Sorry! Location is unavailable."))
               } else {
                  Observable.just(location)
               }
            }
            .map { WalletCoordinates(lat = it.latitude, lng = it.longitude) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }
}
