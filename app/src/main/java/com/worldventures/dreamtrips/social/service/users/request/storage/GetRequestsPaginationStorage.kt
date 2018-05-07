package com.worldventures.dreamtrips.social.service.users.request.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserPaginationStorage
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsPaginationCommand

class GetRequestsPaginationStorage : BaseUserPaginationStorage() {
   override fun getActionClass() = GetRequestsPaginationCommand::class.java
}
