package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultRejectRequestOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): SpecBody.() -> Unit = {
      it("Should remove user from list and shouldn't change canLoadMoreValue") {
         val user = mockUser(4)
         val users = (1..10).map { mockUser(it) }.toMutableList()
         var canLoadMore = true
         DefaultRejectRequestOperation(user).perform(users, { canLoadMore = it })
         assertTrue { users.indexOf(user) == -1 && canLoadMore }
      }
   }
})
