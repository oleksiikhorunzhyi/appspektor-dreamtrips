package com.worldventures.dreamtrips.social.service.users.liker.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.operation.BaseUserStorageOperationTestBody
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class AcceptRequestOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {
   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should add friend to list and change relationship to FRIEND") {
            val user = mockUser(11)
            val users = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true
            AcceptRequestOperation(user).perform(users) { canLoadMore = it }
            val userPos = users.indexOf(user)
            assertTrue { userPos != -1 && users[userPos].relationship == User.Relationship.FRIEND && canLoadMore }
         }
      }
   }
})
