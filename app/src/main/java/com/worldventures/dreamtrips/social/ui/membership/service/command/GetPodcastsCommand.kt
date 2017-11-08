package com.worldventures.dreamtrips.social.ui.membership.service.command

import com.worldventures.core.janet.CommandWithError
import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.janet.dagger.InjectableAction
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction
import com.worldventures.dreamtrips.social.domain.mapping.PodcastsMapper
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast

import javax.inject.Inject

import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import rx.schedulers.Schedulers

private const val PAGE_SIZE = 10

@CommandAction
class GetPodcastsCommand (val refresh: Boolean = false) : CommandWithError<List<Podcast>>(),
      InjectableAction, CachedAction<List<Podcast>> {

   @field:Inject lateinit var janet: Janet
   @field:Inject lateinit var podcastsMapper: PodcastsMapper
   @field:Inject lateinit var db: MediaModelStorage

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
      if (cachedData.isEmpty()) {
         Observable.from(cachedData)
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
      return when (refresh ||  cachedData.isEmpty()) {
         true -> 1
         false -> cachedData.size / PAGE_SIZE + 1
      }
   }

   override fun onRestore(holder: ActionHolder<*>, cache: List<Podcast>) {
      cachedData = ArrayList(cache)
   }

   override fun getCacheData() = ArrayList(result)

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh)
      return ImmutableCacheOptions.builder().params(cacheBundle).build()
   }

   override fun getFallbackErrorMessage() = R.string.error_fail_to_load_podcast
}
