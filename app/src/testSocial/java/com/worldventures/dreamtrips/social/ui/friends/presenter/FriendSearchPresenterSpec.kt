package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendSearchPresenter.View
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetSearchUsersCommand
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import kotlin.test.assertTrue

class FriendSearchPresenterSpec : AbstractUserListPresenterSpec(FriendSearchPresenterTestBody()) {

   class FriendSearchPresenterTestBody : AbstractUserListPresenterTestBody<View, FriendSearchPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("Search users") {
            it("Invoking setQuery() valid query and" +
                  " save input query and notify view with new user data") {
               val query = "friends name"
               presenter.setQuery(query)
               verify(presenter).reload()
               verify(view).refreshUsers(argWhere { friends.size == it.size })
               assertTrue { presenter.query == query }
            }

            it("Invoke setQuery() with query with size < 3 " +
                  "should notify view with empty user data and show empty view") {
               presenter.users = (1..getUsersPerPage()).map { MockUtil.mockUser(it) }.toList()
               presenter.setQuery("")
               verify(view).refreshUsers(argWhere { it.isEmpty() })
               verify(view).updateEmptyCaption(ArgumentMatchers.anyInt())
            }
         }

         describe("Page counter") {
            val query = "friend name"

            it("Should increment nexPage from 1 to 2") {
               assertTrue { presenter.nextPage == 1 }
               presenter.setQuery(query)
               assertTrue { presenter.nextPage == 2 }
            }

            it("Should scroll and increment nexPage from 2 to 3") {
               presenter.setQuery(query)
               assertTrue { presenter.nextPage == 2 }
               presenter.scrolled(friends.size, friends.size - 1)
               assertTrue { presenter.nextPage == 3 }
            }

            it("Error while receiving user data should decrement nexPage") {
               val command = GetSearchUsersCommand(query, 2, getUsersPerPage())
               presenter.setQuery(query)
               assertTrue { presenter.nextPage == 2 }
               presenter.onError(command, Throwable())
               assertTrue { presenter.nextPage == 1 }
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

      override fun getMainDescription() = "FriendSearchPresenter"
   }
}
