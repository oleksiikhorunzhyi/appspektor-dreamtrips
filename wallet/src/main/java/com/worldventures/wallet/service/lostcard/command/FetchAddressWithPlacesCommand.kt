package com.worldventures.wallet.service.lostcard.command

import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace
import com.worldventures.wallet.service.lostcard.command.http.AddressHttpAction
import com.worldventures.wallet.service.lostcard.command.http.PlacesNearbyHttpAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable.zip
import javax.inject.Inject

@CommandAction
class FetchAddressWithPlacesCommand(val coordinates: WalletCoordinates)
   : Command<FetchAddressWithPlacesCommand.PlacesWithAddress>(), InjectableAction,
      CachedAction<Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>> {

   @Inject lateinit var janet: Janet
   @Inject lateinit var mapperyContext: MapperyContext

   private var cachedResult: PlacesWithAddress? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<FetchAddressWithPlacesCommand.PlacesWithAddress>) {
      if (!needApiRequest()) {
         callback.onSuccess(cachedResult)
         return
      }
      zip(
            janet.createPipe(PlacesNearbyHttpAction::class.java)
                  .createObservableResult(PlacesNearbyHttpAction(coordinates))
                  .map { httpAction -> mapperyContext.convert(httpAction.response().locationPlaces, WalletPlace::class.java) },
            janet.createPipe(AddressHttpAction::class.java)
                  .createObservableResult(AddressHttpAction(coordinates))
                  .map { addressAction -> mapperyContext.convert(addressAction.response(), WalletAddress::class.java) }
      ) { locationPlaces, address -> PlacesWithAddress(address, locationPlaces) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun needApiRequest() = cachedResult == null

   override fun getCacheData() = Pair(coordinates, result)

   override fun onRestore(holder: ActionHolder<*>, cache: Pair<WalletCoordinates, FetchAddressWithPlacesCommand.PlacesWithAddress>) {
      if (cache.first == coordinates) {
         cachedResult = cache.second
      }
   }

   override fun getCacheOptions() = CacheOptions(saveToCache = needApiRequest())

   data class PlacesWithAddress internal constructor(val address: WalletAddress?, val places: List<WalletPlace>)
}

