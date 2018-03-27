package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.dreamtrips.api.trip.GetTripRegionsHttpAction
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionModel
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.schedulers.Schedulers
import javax.inject.Inject

@CommandAction
class GetRegionsCommand : Command<List<RegionModel>>(), InjectableAction, CachedAction<List<RegionModel>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mapperyContext: MapperyContext

   private var cachedData: List<RegionModel> = emptyList()

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<RegionModel>>) {
      if (cachedData.isNotEmpty()) callback.onSuccess(cachedData)
      janet.createPipe(GetTripRegionsHttpAction::class.java, Schedulers.io())
            .createObservableResult(GetTripRegionsHttpAction())
            .map { mapperyContext.convert(it.response(), RegionModel::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getCacheData() = result

   override fun onRestore(holder: ActionHolder<*>, cache: List<RegionModel>) {
      cachedData = cache
   }

   override fun getCacheOptions() = CacheOptions()
}
