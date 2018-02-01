package com.worldventures.dreamtrips.social.service.invites

import com.worldventures.dreamtrips.social.service.invites.operation.EmptyOperation
import com.worldventures.janet.cache.CacheOptions
import io.techery.janet.command.annotations.CommandAction

@CommandAction
class ReadMembersCommand : MembersCommand(EmptyOperation()) {

   override fun getCacheOptions() = CacheOptions(saveToCache = false)
}
