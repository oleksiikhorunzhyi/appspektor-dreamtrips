package com.worldventures.dreamtrips.social.ui.tripsimages.service.command

import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.multimedia.GetMultimediaHttpAction
import com.worldventures.dreamtrips.api.multimedia.ImmutableMultimediaPaginatedParams
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.storage.TripImageStorage
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import org.immutables.value.Value
import java.util.ArrayList
import java.util.Date
import javax.inject.Inject

@CommandAction
class GetMemberMediaCommand : BaseMediaCommand, InjectableAction, CachedAction<List<BaseMediaEntity<*>>> {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var mappery: MapperyContext

   private var before: Date? = null
   private var after: Date? = null
   private var perPage: Int = 0

   constructor(args: TripImagesArgs, paginationParams: PaginationParams) : super(args) {
      this.before = paginationParams.before()
      this.after = paginationParams.after()
      this.perPage = paginationParams.perPage()
   }

   constructor(args: TripImagesArgs, fromCacheOnly: Boolean) : super(args) {
      this.fromCacheOnly = fromCacheOnly
   }

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<BaseMediaEntity<*>>>) {
      if (fromCacheOnly) {
         callback.onSuccess(cachedItems)
         return
      }
      if (!cachedItems.isEmpty()) {
         callback.onProgress(0)
      }
      janet.createPipe(GetMultimediaHttpAction::class.java)
            .createObservableResult(GetMultimediaHttpAction(ImmutableMultimediaPaginatedParams.builder()
                  .before(before)
                  .after(after)
                  .pageSize(perPage).build()))
            .map { mappery.convert(it.response(), BaseMediaEntity::class.java) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<BaseMediaEntity<*>>) {
      cachedItems = cache
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(TripImageStorage.PARAM_ARGS, args)
      cacheBundle.put(TripImageStorage.LOAD_MORE, isLoadMore)
      cacheBundle.put(TripImageStorage.RELOAD, isReload)
      cacheBundle.put(TripImageStorage.LOAD_LATEST, false)
      cacheBundle.put(TripImageStorage.REMOVE_ITEMS, false)
      return CacheOptions(saveToCache = !fromCacheOnly, params = cacheBundle)
   }

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_member_images

   override fun lastPageReached() = result != null && result.size < perPage

   @Value.Immutable
   interface PaginationParams {

      fun perPage(): Int

      fun before(): Date?

      fun after(): Date?
   }

}
