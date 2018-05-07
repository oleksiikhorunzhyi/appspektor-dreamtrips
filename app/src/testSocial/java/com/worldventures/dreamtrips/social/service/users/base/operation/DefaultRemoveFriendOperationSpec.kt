package com.worldventures.dreamtrips.social.service.users.base.operation

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class DefaultRemoveFriendOperationSpec : BaseBodySpec(object : BaseUserStorageOperationTestBody() {
   override fun create(): SpecBody.() -> Unit = {
      it("Should remove user from list and change relationship to NONE") {
         val user = mockUser(4).apply { relationship = User.Relationship.FRIEND }
         val users = (1..10).map { mockUser(it) }.toMutableList()
         var canLoadMore = true
         DefaultRemoveFriendOperation(user).perform(users, { canLoadMore = it })
         assertTrue { user.relationship == User.Relationship.NONE && users.indexOf(user) == -1 && canLoadMore }
      }
   }
})
