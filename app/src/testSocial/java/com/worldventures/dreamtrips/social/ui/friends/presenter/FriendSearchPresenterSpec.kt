package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.social.service.users.search.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.users.search.delegate.SearchedUsersStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendSearchPresenter.View
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class FriendSearchPresenterSpec : AbstractUserListPresenterSpec(FriendSearchPresenterTestBody()) {

   class FriendSearchPresenterTestBody : AbstractUserListPresenterTestBody<View, FriendSearchPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("Refresh data") {
            it("Search should notify view with new user data") {
               presenter.takeView(view)
               val query = "friend name"
               presenter.search(query)
               verify(view).refreshUsers(argWhere { it.size == friends.size }, any())
            }

            it("Empty query should notify view with empty list of data") {
               presenter.takeView(view)
               val query = ""
               presenter.search(query)
               verify(view).refreshUsers(argWhere { it.isEmpty() }, any())
            }

            it("Should notify view open circle selector alert") {
               presenter.takeView(view)
               presenter.addUserRequest(user)
               verify(view, VerificationModeFactory.atLeastOnce()).showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
            }
         }
      }

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())

      override fun mockPresenter(): FriendSearchPresenter = spy(FriendSearchPresenter(""))

      override fun mockView(): View = mock()

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetSearchUsersCommand::class.java).result(friends))
         }
      }

      override fun prepareInjection() = super.prepareInjection().apply {
         registerProvider(SearchedUsersStorageDelegate::class.java, { SearchedUsersStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor) })
      }

      override fun getMainDescription() = "FriendSearchPresenter"
   }
}
