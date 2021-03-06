package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.api.friends.GetFriendRequestsHttpAction
import com.worldventures.dreamtrips.api.friends.model.ImmutableGetFriendRequestsParams
import com.worldventures.dreamtrips.social.service.users.base.command.GetUsersCommand
import com.worldventures.janet.cache.CacheBundleImpl
import com.worldventures.janet.cache.CacheOptions
import com.worldventures.janet.cache.CachedAction
import com.worldventures.janet.cache.storage.PaginatedStorage
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import java.util.ArrayList

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

   override fun getFallbackErrorMessage() = R.string.error_failed_to_load_friend_requests

   override fun getCacheData() = ArrayList(result)

   override fun onRestore(holder: ActionHolder<*>, cache: List<User>) {
      cachedUsers = ArrayList(cache)
   }

   override fun getCacheOptions() = CacheOptions(params = CacheBundleImpl().apply {
      put(PaginatedStorage.BUNDLE_REFRESH, isFirstPage)
   })

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
