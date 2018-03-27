package com.worldventures.dreamtrips.social.service.users.request.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperationTestBody
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class AddFriendOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {
   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should add request with OUTGOING_REQUEST relationship") {
            val user = mockUser(11).apply { relationship = User.Relationship.NONE }
            val requests = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true

            AddFriendOperation(user).perform(requests) { canLoadMore = it }
            val userPos = requests.indexOf(user)

            assertTrue { userPos != -1 && requests[userPos].relationship == User.Relationship.OUTGOING_REQUEST && canLoadMore }
         }
      }
   }

})
