package com.worldventures.dreamtrips.social.service.users.liker.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserStorage
import com.worldventures.dreamtrips.social.service.users.liker.command.LikerStorageCommand

class LikersStorage : BaseUserStorage() {
   override fun getActionClass() = LikerStorageCommand::class.java
}
