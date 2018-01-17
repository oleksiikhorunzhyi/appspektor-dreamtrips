package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.multimedia.GetUserMultimediaHttpAction
import com.worldventures.dreamtrips.api.multimedia.ImmutableMultimediaPaginatedParams
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import java.util.ArrayList
import java.util.Date
import javax.inject.Inject

@CommandAction
class GetUsersMediaCommand : BaseMediaCommand, InjectableAction, CachedAction<List<BaseMediaEntity<*>>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   private var userId = 0
   private var perPage = 0
   private var before: Date? = null
   private var after: Date? = null

   constructor(args: TripImagesArgs, paginationParams: GetMemberMediaCommand.PaginationParams) : super(args) {
      this.userId = args.userId
      this.perPage = args.pageSize
      this.before = paginationParams.before()
      this.after = paginationParams.after()
   }

   constructor(args: TripImagesArgs, fromCacheOnly: Boolean) : super(args) {
      this.fromCacheOnly = fromCacheOnly
   }

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<BaseMediaEntity<*>>>) {
      if (fromCacheOnly) {
         callback.onSuccess(cachedItems)
         return
      } else {
         if (!cachedItems.isEmpty()) {
            callback.onProgress(0)
         }
         janet.createPipe(GetUserMultimediaHttpAction::class.java)
               .createObservableResult(GetUserMultimediaHttpAction(userId, ImmutableMultimediaPaginatedParams.builder()
                     .before(before)
                     .after(after)
                     .pageSize(perPage).build()))
               .map { mappery.convert(it.response(), BaseMediaEntity::class.java) }
               .subscribe(callback::onSuccess, callback::onFail)
      }
   }

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<BaseMediaEntity<*>>) {
      this.cachedItems = cache
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(TripImageStorage.PARAM_ARGS, args)
      cacheBundle.put(TripImageStorage.RELOAD, isReload)
      cacheBundle.put(TripImageStorage.LOAD_MORE, isLoadMore)
      cacheBundle.put(TripImageStorage.LOAD_LATEST, false)
      cacheBundle.put(TripImageStorage.REMOVE_ITEMS, false)
      return ImmutableCacheOptions.builder()
            .params(cacheBundle)
            .saveToCache(!fromCacheOnly)
            .build()
   }

   override fun lastPageReached() = result != null && result.size < perPage

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_member_images
}
