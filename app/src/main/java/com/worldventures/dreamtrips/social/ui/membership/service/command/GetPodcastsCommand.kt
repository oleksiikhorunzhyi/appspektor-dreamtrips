package com.worldventures.dreamtrips.social.ui.membership.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction
import com.worldventures.dreamtrips.social.domain.mapping.PodcastsMapper
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.PaginatedStorage
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject

private const val PAGE_SIZE = 10

@CommandAction
class GetPodcastsCommand(val refresh: Boolean = false) : CommandWithError<List<Podcast>>(), InjectableAction, CachedAction<List<Podcast>> {

   @Inject lateinit var janet: Janet
   @Inject lateinit var podcastsMapper: PodcastsMapper
   @Inject lateinit var db: MediaModelStorage

   private var cachedData = ArrayList<Podcast>()

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<Podcast>>) {
      initialProcessCache(callback)

      janet.createPipe(GetPodcastsHttpAction::class.java, Schedulers.io())
            .createObservableResult(GetPodcastsHttpAction(obtainPage(), PAGE_SIZE))
            .flatMap { Observable.from(it.response()) }
            .map { podcastsMapper.map(it) }
            .doOnNext { connectCachedEntity(it) }
            .toList()
            .doOnNext { clearCacheIfNeeded() }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun initialProcessCache(callback: Command.CommandCallback<List<Podcast>>) {
      if (!cachedData.isEmpty()) {
         Observable.from(cachedData)
               .doOnNext { connectCachedEntity(it) }
               .toList()
               .doOnNext { callback.onProgress(0) }
               .subscribe()
      }
   }

   private fun connectCachedEntity(podcast: Podcast) {
      val cachedModel = db.getDownloadMediaModel(podcast.fileUrl)
      if (cachedModel != null) podcast.cachedModel = cachedModel
   }

   private fun clearCacheIfNeeded() {
      if (refresh) cachedData.clear()
   }

   fun getItems(): List<Podcast> {
      val podcasts = mutableListOf<Podcast>()
      podcasts.addAll(cachedData)
      result?.let { podcasts.addAll(it) }
      return podcasts
   }

   fun hasMore() = result.size == PAGE_SIZE

   private fun obtainPage(): Int {
      return when (refresh || cachedData.isEmpty()) {
         true -> 1
         false -> cachedData.size / PAGE_SIZE + 1
      }
   }

   override fun onRestore(holder: ActionHolder<*>, cache: List<Podcast>) {
      cachedData = ArrayList(cache)
   }

   override fun getCacheData() = ArrayList(result)

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl().apply {
      put(PaginatedStorage.BUNDLE_REFRESH, refresh)
   })

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_podcast
}
