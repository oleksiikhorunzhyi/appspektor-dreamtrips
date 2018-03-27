package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultAddFriendOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should change user relationship to OUTGOING_REQUEST") {
            val user = mockUser(4)
            val users = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true
            DefaultAddFriendOperation(user).perform(users) { canLoadMore = it }
            assertTrue {
               users[users.indexOf(user)].relationship == User.Relationship.OUTGOING_REQUEST
                     && canLoadMore
            }
         }
      }
   }
})
