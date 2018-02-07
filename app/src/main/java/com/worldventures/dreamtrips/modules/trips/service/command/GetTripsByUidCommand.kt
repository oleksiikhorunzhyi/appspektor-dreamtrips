package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.trip.GetTripsDetailsHttpAction
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.service.storage.TripsByUidsStorage
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class GetTripsByUidCommand(private val tripUids: List<String>)
   : CommandWithError<List<TripModel>>(), InjectableAction, CachedAction<List<TripModel>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   private var cachedTrips: List<TripModel> = emptyList()

   val items: List<TripModel>
      get() = result ?: cachedTrips

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<TripModel>>) {
      if (hasValidCachedItems()) callback.onProgress(0)
      janet.createPipe(GetTripsDetailsHttpAction::class.java)
            .createObservableResult(GetTripsDetailsHttpAction(tripUids))
            .map { it.response() }
            .map { mappery.convert(it, TripModel::class.java) }
            .subscribe(CommandActionBaseHelper.ActionCommandSubscriber.wrap(callback))
   }

   fun hasValidCachedItems() = cachedTrips.isNotEmpty() && requestedSizeEqualsCached()

   private fun requestedSizeEqualsCached() = cachedTrips.size == tripUids.size

   override fun getFallbackErrorMessage() = R.string.string_failed_to_load_trips

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<TripModel>) {
      cachedTrips = cache
   }

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl().apply {
      put(TripsByUidsStorage.TRIP_UIDS, tripUids)
   })
}
