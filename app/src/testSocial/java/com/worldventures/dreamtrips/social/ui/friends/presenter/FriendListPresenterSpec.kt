package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedPresenterSpek
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendListPresenter.View
import com.worldventures.dreamtrips.social.ui.friends.service.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertTrue

class FriendListPresenterSpec : AbstractUserListPresenterSpec(FriendListPresenterTestBody()) {

   class FriendListPresenterTestBody : AbstractUserListPresenterTestBody<View, FriendListPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("View taken") {
            it("Should subscribe on accept friend command") {
               verify(friendInteractor).acceptRequestPipe()
            }

            it("Should invoke getFriendsPipe()") {
               verify(friendInteractor, VerificationModeFactory.atLeastOnce()).friendsPipe
            }

            it("View should receive new users data") {
               verify(view).refreshUsers(argWhere {
                  it.size == friends.size
               })
            }
         }

         describe("Refresh friends") {
            it("Invoking reloadWithFilter() should notify view with new user data and contains input args") {
               presenter.reloadWithFilter(FeedPresenterSpek.circles[0], 1)
               verify(view, VerificationModeFactory.times(2))
                     .refreshUsers(argWhere { it.size == friends.size })
               assertTrue { presenter.selectedCircle.id == FeedPresenterSpek.circles[0].id && presenter.position == 1 }
            }

            it("Invoking setQuery() should notify view with new user data and contains input query") {
               val query = "friend name"
               presenter.setQuery(query)
               verify(view, VerificationModeFactory.times(2))
                     .refreshUsers(argWhere { it.size == friends.size })
               assertTrue { presenter.query == query }
            }
         }

         describe("Show filters") {
            it("Presenter should invoke getCirclesObservable() and notify view to open filters") {
               presenter.onFilterClicked()
               verify(presenter).circlesObservable
               verify(view).showFilters(argWhere { it.size == circles.size }, ArgumentMatchers.anyInt())
            }
         }
      }

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())

      override fun init() {
         super.init()
         presenter.onInjected()
      }

      override fun mockPresenter(): FriendListPresenter = spy(FriendListPresenter())

      override fun mockView(): View = mock()

      override fun mockActionService(): MockCommandActionService.Builder = super.mockActionService().apply {
         addContract(BaseContract.of(ActOnFriendRequestCommand.Accept::class.java).result(user))
         addContract(BaseContract.of(GetFriendsCommand::class.java).result(friends))
      }

      override fun getMainDescription() = "FriendListPresenter"
   }
}

