package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import java.util.ArrayList

@CommandAction
class UserImagesRemovedCommand(internal var tripImagesArgs: TripImagesArgs, internal var baseMediaEntities: List<BaseMediaEntity<*>>)
   : Command<List<BaseMediaEntity<*>>>(), CachedAction<List<BaseMediaEntity<*>>> {

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<BaseMediaEntity<*>>>) = callback.onSuccess(baseMediaEntities)

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<BaseMediaEntity<*>>) {
      //do nothing
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(TripImageStorage.PARAM_ARGS, tripImagesArgs)
      cacheBundle.put(TripImageStorage.LOAD_LATEST, false)
      cacheBundle.put(TripImageStorage.RELOAD, false)
      cacheBundle.put(TripImageStorage.LOAD_MORE, false)
      cacheBundle.put(TripImageStorage.REMOVE_ITEMS, true)
      return CacheOptions(restoreFromCache = false, params = cacheBundle)
   }
}
