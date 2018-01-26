package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultDeleteRequestOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {
   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should remove user from list and shouldn't change canLoadMoreValue") {
            val user = mockUser(4)
            val users = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true
            DefaultDeleteRequestOperation(user).perform(users, { canLoadMore = it })
            assertTrue { users.indexOf(user) == -1 && canLoadMore }
         }
      }
   }
})
