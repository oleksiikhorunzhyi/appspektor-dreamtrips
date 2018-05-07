package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultAcceptAllOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should change Relationship.INCOMING_REQUEST to Relationship.Friend") {
            val requests = (1..10).map { mockUser(it) }.toMutableList()
            var canLoadMore = true
            DefaultAcceptAllOperation().perform(requests, { canLoadMore = it })

            assertTrue {
               requests.filter { it.relationship == User.Relationship.FRIEND }
                     .size == requests.size / 2 && canLoadMore
            }
         }
      }
   }

   override fun mockUser(id: Int) = super.mockUser(id).apply {
      relationship = if (id % 2 == 0) User.Relationship.INCOMING_REQUEST
      else User.Relationship.OUTGOING_REQUEST
   }
})
