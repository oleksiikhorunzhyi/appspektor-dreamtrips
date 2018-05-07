package com.worldventures.dreamtrips.modules.trips.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction
import com.worldventures.dreamtrips.modules.trips.model.TripModel
import com.worldventures.dreamtrips.modules.trips.service.storage.TripDetailsStorage
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

@CommandAction
class GetTripDetailsCommand(private val uid: String) : CommandWithError<TripModel>(), InjectableAction, CachedAction<TripModel> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   var cachedModel: TripModel? = null
      private set

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<TripModel>) {
      if (cachedModel != null) callback.onProgress(0)
      janet.createPipe(GetTripHttpAction::class.java)
            .createObservableResult(GetTripHttpAction(uid))
            .map { it.response() }
            .map { mappery.convert(it, TripModel::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_item_details

   override fun getCacheData() = result

   override fun onRestore(holder: ActionHolder<*>, cache: TripModel) {
      cachedModel = cache
   }

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl()
         .apply { put(TripDetailsStorage.UID, uid) })
}
