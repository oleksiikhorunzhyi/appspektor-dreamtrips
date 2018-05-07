package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.trip.GetTripsHttpAction
import com.worldventures.dreamtrips.api.trip.ImmutableGetTripsHttpAction
import com.worldventures.dreamtrips.core.janet.CommandActionBaseHelper.ActionCommandSubscriber
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.model.filter.TripsFilterData
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class GetTripsCommand(private val searchQuery: String,
                      private val tripsFilterData: TripsFilterData,
                      private val refresh: Boolean)
   : CommandWithError<List<TripModel>>(), InjectableAction, CachedAction<List<TripModel>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   private var cachedData: List<TripModel> = emptyList()

   private val page: Int
      get() = if (refresh || cachedData.isEmpty()) 1 else cachedData.size / PER_PAGE + 1

   val items: List<TripModel>
      get() {
         val trips = ArrayList<TripModel>()
         trips.addAll(cachedData)
         result?.apply { trips.addAll(this) }
         return trips
      }

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<TripModel>>) {
      if (!cachedData.isEmpty()) callback.onProgress(0)
      janet.createPipe(GetTripsHttpAction::class.java)
            .createObservableResult(GetTripsHttpAction(params(tripsFilterData, searchQuery, page)))
            .map { it.response() }
            .map { mappery.convert(it, TripModel::class.java) }
            .doOnNext { clearCacheIfNeeded() }
            .subscribe(ActionCommandSubscriber.wrap(callback))
   }

   private fun clearCacheIfNeeded() {
      if (refresh) cachedData = emptyList()
   }

   private fun params(tripsFilterData: TripsFilterData?, query: String, page: Int): ImmutableGetTripsHttpAction.Params {
      val params = ImmutableGetTripsHttpAction.Params.builder().page(page).perPage(PER_PAGE).query(query)
      tripsFilterData?.apply {
         params.durationMin(minNightsForRequest)
               .durationMax(maxNightsForRequest)
               .priceMin(minPriceForRequest)
               .priceMax(maxPriceForRequest)
               .startDate(startDateForRequest)
               .endDate(endDateForRequest)
               .regions(acceptedRegions)
               .activities(acceptedActivities)
               .soldOut(isShowSoldOut)
               .recentFirst(isShowRecentlyAdded)
               .liked(isShowFavorites)
      }
      return params.build()
   }

   fun hasMore() = result.size == PER_PAGE

   override fun getFallbackErrorMessage() = R.string.string_failed_to_load_trips

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<TripModel>) {
      cachedData = cache
   }

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl().apply {
      put(PaginatedStorage.BUNDLE_REFRESH, refresh)
   })

   companion object {
      val PER_PAGE = 20
   }
}
