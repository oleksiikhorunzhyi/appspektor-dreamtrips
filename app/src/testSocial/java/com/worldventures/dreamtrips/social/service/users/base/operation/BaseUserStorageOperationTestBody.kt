package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseTestBody

abstract class BaseUserStorageOperationTestBody : BaseTestBody {

   protected open fun setup() {

   }

   protected open fun mockUser(id: Int) = User().apply {
      firstName = "Name " + id.toString()
      lastName = "LastName " + id.toString()
      this.id = id
   }
}
