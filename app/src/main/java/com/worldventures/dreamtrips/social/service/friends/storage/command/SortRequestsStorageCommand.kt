package com.worldventures.dreamtrips.social.service.friends.storage.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.friends.model.AcceptanceHeaderModel
import com.worldventures.dreamtrips.social.service.friends.model.RequestHeaderModel
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class SortRequestsStorageCommand(private val isRefresh: Boolean
                                 , private val users: List<User>
                                 , private val outgoingHeaderTitle: String
                                 , private val incomingHeaderTitle: String)
   : Command<List<Any>>(), CachedAction<Int> {

   private var incomingCount = 0

   override fun run(callback: CommandCallback<List<Any>>?) {
      val sortedItems = mutableListOf<Any>()
      val incomingItems = users.filter { it.relationship == User.Relationship.INCOMING_REQUEST }
      val acceptedCount = (incomingCount - incomingItems.size).let { if (it > 0) it else 0 }
      incomingCount = incomingItems.size

      if (acceptedCount != 0 || incomingItems.isNotEmpty()) {
         val incomingHeader = RequestHeaderModel(incomingHeaderTitle)
         incomingHeader.count = incomingCount
         sortedItems.add(incomingHeader)
      }

      if (acceptedCount != 0) {
         sortedItems.add(AcceptanceHeaderModel(acceptedCount))
      } else if (incomingItems.isNotEmpty()) {
         sortedItems.addAll(incomingItems)
      }

      val outgoing = users.filter {
         it.relationship == User.Relationship.OUTGOING_REQUEST
               || it.relationship == User.Relationship.REJECTED
      }

      if (outgoing.isNotEmpty()) {
         sortedItems.add(RequestHeaderModel(outgoingHeaderTitle))
         sortedItems.addAll(outgoing)
      }

      callback?.onSuccess(sortedItems)
   }

   override fun getCacheData() = incomingCount

   override fun onRestore(holder: ActionHolder<*>?, cache: Int?) {
      incomingCount = cache ?: 0
   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .params(CacheBundleImpl().apply { put(PaginatedStorage.BUNDLE_REFRESH, isRefresh) }).build()
   }
}
