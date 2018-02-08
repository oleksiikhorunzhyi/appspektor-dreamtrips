package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.trip.GetTripsLocationsHttpAction
import com.worldventures.dreamtrips.api.trip.ImmutableGetTripsLocationsHttpAction
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData
import com.worldventures.dreamtrips.modules.trips.model.map.Pin
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
class GetTripsLocationsCommand(private val searchQuery: String, private val tripsFilterData: TripsFilterData?) : CommandWithError<List<Pin>>(), InjectableAction, CachedAction<List<Pin>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   private var cachedResult: List<Pin> = emptyList()

   private val params: GetTripsLocationsHttpAction.Params
      get() = if (tripsFilterData == null) ImmutableGetTripsLocationsHttpAction.Params.builder().build()
      else ImmutableGetTripsLocationsHttpAction.Params.builder()
            .durationMax(tripsFilterData.maxNights)
            .durationMin(tripsFilterData.minNights)
            .priceMin(tripsFilterData.minPrice)
            .priceMax(tripsFilterData.maxPrice)
            .liked(tripsFilterData.isShowFavorites)
            .soldOut(tripsFilterData.isShowSoldOut)
            .recentFirst(tripsFilterData.isShowRecentlyAdded)
            .endDate(tripsFilterData.endDate)
            .startDate(tripsFilterData.startDate)
            .query(searchQuery)
            .activities(tripsFilterData.acceptedActivities)
            .regions(tripsFilterData.acceptedRegions)
            .build()

   val items: List<Pin>
      get() = result ?: cachedResult

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Pin>>) {
      if (!cachedResult.isEmpty()) callback.onProgress(0)
      janet.createPipe(GetTripsLocationsHttpAction::class.java)
            .createObservableResult(GetTripsLocationsHttpAction(params))
            .map { it.response() }
            .map { mappery.convert(it, Pin::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.string_failed_to_load_trips

   override fun onRestore(holder: ActionHolder<*>, cache: List<Pin>) {
      cachedResult = cache
   }

   override fun getCacheOptions() = CacheOptions()

   override fun getCacheData() = ArrayList(result)
}
