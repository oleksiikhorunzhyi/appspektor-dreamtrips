package com.worldventures.dreamtrips.social.service.users.request.storage

import com.worldventures.dreamtrips.social.service.users.base.storage.BaseUserStorage
import com.worldventures.dreamtrips.social.service.users.request.command.UserRequestsStorageCommand

class UserRequestsStorage : BaseUserStorage() {
   override fun getActionClass() = UserRequestsStorageCommand::class.java
}
