package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel
import com.worldventures.dreamtrips.modules.trips.model.filter.CachedTripFilters
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionModel
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

@CommandAction
class GetTripsFilterDataCommand : CommandWithError<CachedTripFilters>(), InjectableAction, CachedAction<CachedTripFilters> {

   @Inject internal lateinit var janet: Janet

   private var cachedTripFilters: CachedTripFilters? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<CachedTripFilters>) {
      if (cachedTripFilters != null) callback.onProgress(0)

      val activitiesActionPipe = janet.createPipe(GetActivitiesCommand::class.java, Schedulers.io())
      val regionsActionPipe = janet.createPipe(GetRegionsCommand::class.java, Schedulers.io())

      val activitiesObservable = activitiesActionPipe.createObservableResult(GetActivitiesCommand()).map { it.result }
      val regionsObservable = regionsActionPipe.createObservableResult(GetRegionsCommand()).map { it.result }

      Observable.zip<List<RegionModel>, List<ActivityModel>, CachedTripFilters>(regionsObservable, activitiesObservable)
      { regions, activities -> CachedTripFilters(activities, regions) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_activities

   override fun getCacheData() = result

   override fun onRestore(holder: ActionHolder<*>, cache: CachedTripFilters) {
      this.cachedTripFilters = cache
   }

   override fun getCacheOptions() = CacheOptions()
}
