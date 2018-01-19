package com.worldventures.dreamtrips.social.service.users.base.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperation
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction

@CommandAction
abstract class BaseUserStorageCommand(protected val operation: BaseUserStorageOperation) : Command<List<User>>(),
      CachedAction<BaseUserStorageCommand.UsersCachedModel> {

   protected val items = mutableListOf<User>()
   protected var haveMoreItems = true
   val isRefresh = operation.refresh

   override fun run(callback: CommandCallback<List<User>>) {
      callback.onSuccess(operation.perform(items, this::haveMoreItemsAction))
   }

   protected open fun haveMoreItemsAction(haveMoreItems: Boolean) {
      this.haveMoreItems = haveMoreItems
   }

   override fun onRestore(holder: ActionHolder<*>?, cache: UsersCachedModel?) {
      items.clear()
      items.addAll(cache?.items ?: mutableListOf())
      haveMoreItems = cache?.haveMoreItems ?: true
   }

   override fun getCacheData() = UsersCachedModel(items, haveMoreItems)

   open fun getStorageItems() = ArrayList(items)

   fun isNoMoreItems() = !haveMoreItems

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions.builder()
            .params(CacheBundleImpl().apply {
               put(PaginatedStorage.BUNDLE_REFRESH, isRefresh)
            }).build()
   }

   open class UsersCachedModel(val items: MutableList<User>, val haveMoreItems: Boolean)
}
