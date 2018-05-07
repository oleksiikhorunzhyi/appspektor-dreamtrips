package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultChangeCircleOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {

   override fun create(): Spec.() -> Unit = {
      describe("Perform operation") {
         it("Should change circle in user in the list") {
            val users = (1..10).map { mockUser(it) }.toMutableList()
            val circle = Circle.withTitle("friend").apply { id = "test" }
            var canLoadMore = true
            DefaultChangeCircleOperation(4) { it.apply { add(circle) } }.perform(users) { canLoadMore = it }
            val user = users.firstOrNull { it.id == 4 }
            assertTrue { user?.circles?.indexOf(circle) ?: -1 != -1 && canLoadMore }
         }
      }
   }

   override fun mockUser(id: Int) = super.mockUser(id).apply {
      circles = ArrayList()
   }

})
