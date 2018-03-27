package com.worldventures.dreamtrips.social.service.users.liker.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserPaginationStorage
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersPaginationCommand

class GetLikersPaginationStorage : BaseUserPaginationStorage() {
   override fun getActionClass() = GetLikersPaginationCommand::class.java
}
