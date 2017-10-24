package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.core.janet.cache.CacheBundleImpl
import com.worldventures.core.janet.cache.CacheOptions
import com.worldventures.core.janet.cache.ImmutableCacheOptions
import com.worldventures.dreamtrips.social.service.invites.operation.EmptyOperation
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class ReadMembersCommand : MembersCommand(EmptyOperation()) {

   override fun getCacheOptions(): CacheOptions = ImmutableCacheOptions.builder().saveToCache(false).params(CacheBundleImpl()).build()
}