package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.dreamtrips.api.trip.GetTripActivitiesHttpAction
import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel
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
class GetActivitiesCommand : Command<List<ActivityModel>>(), InjectableAction, CachedAction<List<ActivityModel>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mapperyContext: MapperyContext

   private var cachedResult: List<ActivityModel> = emptyList()

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<ActivityModel>>) {
      if (cachedResult.isNotEmpty()) callback.onSuccess(cachedResult)
      janet.createPipe(GetTripActivitiesHttpAction::class.java, Schedulers.io())
            .createObservableResult(GetTripActivitiesHttpAction())
            .map { mapperyContext.convert(it.response(), ActivityModel::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getCacheData() = result

   override fun onRestore(holder: ActionHolder<*>, cache: List<ActivityModel>) {
      cachedResult = cache
   }

   override fun getCacheOptions() = CacheOptions()
}
