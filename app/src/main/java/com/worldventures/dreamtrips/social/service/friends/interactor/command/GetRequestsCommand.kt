package com.worldventures.dreamtrips.social.service.friends.interactor.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.GetFriendRequestsHttpAction
import com.worldventures.dreamtrips.api.friends.model.ImmutableGetFriendRequestsParams
import java.util.ArrayList
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class GetRequestsCommand(val page: Int) : GetUsersCommand(), CachedAction<List<User>> {

   private var cachedUsers: List<User>? = null

   val isFirstPage: Boolean
      get() = page == 1

   //due to details of backend implementation,
   //we assume that there are no more items,
   //if result is empty
   val isNoMoreElements: Boolean
      get() = result.isEmpty()

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<List<User>>) {
      janet.createPipe(GetFriendRequestsHttpAction::class.java)
            .createObservableResult(GetFriendRequestsHttpAction(ImmutableGetFriendRequestsParams.builder()
                  .page(page)
                  .perPage(PER_PAGE)
                  .build()))
            .map { it.response() }
            .map { this.convert(it) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getFallbackErrorMessage(): Int = R.string.error_failed_to_load_friend_requests

   override fun getCacheData(): List<User> = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<User>) {
      cachedUsers = ArrayList(cache)
   }

   override fun getCacheOptions(): CacheOptions {
      val cacheBundle = CacheBundleImpl()
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isFirstPage)
      return ImmutableCacheOptions.builder().params(cacheBundle).build()
   }

   fun items(): List<User> {
      //we should add previous loaded pages in beginning of list
      //and avoid situation when we add entire cache of previous loading session
      return ArrayList<User>().apply {
         if (result != null) {
            if (!isFirstPage && cachedUsers != null) this.addAll(cachedUsers ?: ArrayList())
            this.addAll(result)
         } else if (cachedUsers != null) this.addAll(cachedUsers ?: ArrayList())
      }
   }

   companion object {
      private val PER_PAGE = 100
   }

}

