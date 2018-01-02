package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs

import java.util.ArrayList

import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class MemberImagesAddedCommand(internal var tripImagesArgs: TripImagesArgs, internal var baseMediaEntities: List<BaseMediaEntity<*>>)
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
      cacheBundle.put(TripImageStorage.LOAD_LATEST, true)
      cacheBundle.put(TripImageStorage.RELOAD, false)
      cacheBundle.put(TripImageStorage.LOAD_MORE, false)
      cacheBundle.put(TripImageStorage.REMOVE_ITEMS, false)
      return ImmutableCacheOptions.builder()
            .params(cacheBundle)
            .restoreFromCache(false)
            .build()
   }
}
