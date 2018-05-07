package com.worldventures.dreamtrips.social.service.users.liker.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperationTestBody
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class RemoveFriendOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): SpecBody.() -> Unit = {
      describe("Perform operation") {
         it("Should change user relationship to none") {
            val user = mockUser(4)
            val users = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true
            RemoveFriendOperation(user).perform(users) { canLoadMore = it }
            val userPos = users.indexOf(user)
            assertTrue { userPos != -1 && users[userPos].relationship == User.Relationship.NONE && canLoadMore }
         }
      }
   }
})
