package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.ysbh.GetYSBHPhotosHttpAction
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.YsbhPhotoStorage
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.ArrayList
import javax.inject.Inject

@CommandAction
class GetYSBHPhotosCommand : CommandWithError<List<YSBHPhoto>>(), InjectableAction, CachedAction<List<YSBHPhoto>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   var page: Int = 0

   var isFromCache: Boolean = false
      private set
   private var cachedItems: List<YSBHPhoto>? = null

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<YSBHPhoto>>) {
      if (isFromCache && cachedItems != null) {
         callback.onSuccess(cachedItems)
      } else {
         janet.createPipe(GetYSBHPhotosHttpAction::class.java)
               .createObservableResult(GetYSBHPhotosHttpAction(page, PER_PAGE))
               .map { it.response() }
               .map { mappery.convert(it, YSBHPhoto::class.java) }
               .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
      }
   }

   fun lastPageReached() = result.size < PER_PAGE

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_member_images

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<YSBHPhoto>) {
      cachedItems = cache
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(YsbhPhotoStorage.RELOAD, page == 1)
      cacheBundle.put(YsbhPhotoStorage.LOAD_MORE, page != 1)
      return ImmutableCacheOptions.builder()
            .saveToCache(!isFromCache)
            .params(cacheBundle)
            .build()
   }

   companion object {

      const val PER_PAGE = 40

      fun cachedCommand(): GetYSBHPhotosCommand {
         val getYSBHPhotosCommand = GetYSBHPhotosCommand()
         getYSBHPhotosCommand.isFromCache = true
         return getYSBHPhotosCommand
      }

      fun commandForPage(page: Int): GetYSBHPhotosCommand {
         val getYSBHPhotosCommand = GetYSBHPhotosCommand()
         getYSBHPhotosCommand.page = page
         return getYSBHPhotosCommand
      }
   }
}
