package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle
import com.worldventures.dreamtrips.social.ui.friends.presenter.BaseUserListPresenter.View
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class UsersLikedItemPresenterSpec : AbstractUserListPresenterSpec(UsersLikedItemPresenterTestBody()) {

   class UsersLikedItemPresenterTestBody : AbstractUserListPresenterTestBody<View, UsersLikedItemPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("View taken") {
            it("Should notify view with new user data") {
               verify(view).refreshUsers(argWhere { it.size == friends.size })
            }
         }

         describe("Change user state") {
            it("Should change the user in list with new relationship") {
               presenter.users = users
               val user = mockUser(10)
               user.relationship = User.Relationship.OUTGOING_REQUEST
               presenter.userStateChanged(user)
               assertTrue {
                  presenter.users.any {
                     (user.id == it.id && user.relationship
                           == User.Relationship.OUTGOING_REQUEST)
                  }
               }
            }
         }
      }

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())

      override fun mockPresenter(): UsersLikedItemPresenter {
         return spy(UsersLikedItemPresenter(UsersLikedEntityBundle(mock(), 1)))
      }

      override fun mockView(): View = mock()

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetLikersCommand::class.java).result(friends))
         }
      }

      override fun getMainDescription() = "UsersLikedItemPresenter"
   }
}
