package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.model.User
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.PaginatedStorage
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

@CommandAction
class UserPaginationCommand(private val refresh: Boolean = true, private val getUserOperation: (page: Int, perPage: Int)
-> Observable<out GetUsersCommand>) : Command<List<User>>(), CachedAction<UserPaginationCommand.PaginationCachedModel> {

   private var page = 1
   private var perPage = 100
   private var canLoadMore = true

   val isFirstPage
      get() = page == 1

   override fun run(callback: CommandCallback<List<User>>) {
      if (!canLoadMore) return
      getUserOperation.invoke(page, perPage)
            .map { it.result.apply { canLoadMore = isNotEmpty() } }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getCacheData() = PaginationCachedModel(page + 1, canLoadMore)

   override fun onRestore(holder: ActionHolder<*>?, cache: PaginationCachedModel?) {
      page = cache?.page ?: 1
      canLoadMore = cache?.canLoadMore ?: true
   }

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl().apply {
      put(PaginatedStorage.BUNDLE_REFRESH, refresh)
   })

   class PaginationCachedModel(val page: Int, val canLoadMore: Boolean)
}
