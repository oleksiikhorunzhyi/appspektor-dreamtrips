package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultUpdateListOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): SpecBody.() -> Unit = {
      describe("Perform operation") {
         it("Should update list with data and change canLoadMore to true") {
            val emptyLis: MutableList<User> = mutableListOf()
            val users = (1..10).map { mockUser(it) }.toList()
            var canLoadMore = false
            DefaultUpdateListOperation(users, true).perform(emptyLis) { canLoadMore = it }
            assertTrue { emptyLis.size == users.size && canLoadMore }
         }

         it("Should notify that no more items") {
            var canLoadMore = true
            DefaultUpdateListOperation(listOf(), false).perform(mutableListOf()) { canLoadMore = it }
            assertFalse { canLoadMore }
         }
      }
   }

})
