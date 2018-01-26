package com.worldventures.dreamtrips.social.service.users.base.command

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.CachedAction
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.core.janet.cache.storage.PaginatedStorage
import com.worldventures.core.model.User
import io.techery.janet.ActionHolder
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable

abstract class BaseUserPaginationCommand(val refresh: Boolean, private val getUserOperation: (page: Int, perPage: Int)
-> Observable<out GetUsersCommand>) : Command<List<User>>(), CachedAction<BaseUserPaginationCommand.PaginationCachedModel> {

   var page = 1; private set
   private var perPage = 100

   override fun run(callback: CommandCallback<List<User>>) {
      getUserOperation.invoke(page, perPage)
            .map { it.result }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   override fun getCacheData() = PaginationCachedModel(page + 1)

   override fun onRestore(holder: ActionHolder<*>?, cache: PaginationCachedModel?) {
      page = cache?.page ?: 1
   }

   override fun getCacheOptions(): CacheOptions {
      return ImmutableCacheOptions
            .builder()
            .params(CacheBundleImpl().apply {
               put(PaginatedStorage.BUNDLE_REFRESH, refresh)
            }).build()
   }

   @CommandAction
   class PaginationCachedModel(val page: Int)

}

