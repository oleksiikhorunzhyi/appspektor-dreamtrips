package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.inspirations.GetInspireMePhotosHttpAction
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.InspireMeStorage
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class GetInspireMePhotosCommand : CommandWithError<List<Inspiration>>(), InjectableAction, CachedAction<List<Inspiration>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   var page: Int = 0
   private var randomSeed: Double = 0.0 // used later when API lib is fixed for inspire me photos

   var isFromCache: Boolean = false
      private set
   private var cachedItems: List<Inspiration>? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Inspiration>>) {
      if (isFromCache && cachedItems != null) {
         callback.onSuccess(cachedItems)
      } else {
         janet.createPipe(GetInspireMePhotosHttpAction::class.java)
               .createObservableResult(GetInspireMePhotosHttpAction(randomSeed, page, PER_PAGE))
               .map { mappery.convert(it.response(), Inspiration::class.java) }
               .subscribe(callback::onSuccess, callback::onFail)
      }
   }

   fun lastPageReached() = result.size < PER_PAGE

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<Inspiration>) {
      cachedItems = cache
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(InspireMeStorage.RELOAD, page == 1)
      cacheBundle.put(InspireMeStorage.LOAD_MORE, page != 1)
      return ImmutableCacheOptions.builder().saveToCache(!isFromCache).params(cacheBundle).build()
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_inspire_images

   companion object {

      const val PER_PAGE = 40

      fun cachedCommand(): GetInspireMePhotosCommand {
         val command = GetInspireMePhotosCommand()
         command.isFromCache = true
         return command
      }

      fun forPage(randomSeed: Double, page: Int): GetInspireMePhotosCommand {
         val command = GetInspireMePhotosCommand()
         command.page = page
         command.randomSeed = randomSeed
         return command
      }
   }
}
