package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.FriendsListStorageDelegate
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.ui.friends.presenter.FriendListPresenter.View
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
         describe("Refresh data") {
            it("Presenter should reload data") {
               presenter.takeView(view)
               verify(presenter).reload()
            }

            it("View should receive new users data") {
               presenter.takeView(view)
               verify(view).refreshUsers(argWhere { it.size == friends.size })
            }

            it("Apply filters should notify view with new user data and contains input args") {
               presenter.takeView(view)
               presenter.reloadWithFilter(circles[0], 1)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { it.size == friends.size })
               assertTrue { presenter.selectedCircle?.id == circles[0].id && presenter.position == 1 }
            }

            it("Scrolling to last item must notify view new part of data") {
               presenter.takeView(view)
               presenter.scrolled(100, 100)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { friends.size == it.size })
            }

            it("Search should notify view with new user data") {
               presenter.takeView(view)
               val query = "friend name"
               presenter.search(query)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { it.size == friends.size })
            }

            it("Empty query shouldn't initiate receive new part of data") {
               presenter.takeView(view)
               val query = ""
               presenter.search(query)
               verify(view, VerificationModeFactory.times(1)).refreshUsers(argWhere { it.size == friends.size })
            }

            it("Removing friend should notify view by data without it user") {
               presenter.takeView(view)
               presenter.unfriend(user)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { it.indexOf(user) == -1 })
            }
         }

         describe("Show filters") {
            it("Presenter should notify view to open filters") {
               presenter.takeView(view)
               presenter.onFilterClicked()
               verify(view).showFilters(argWhere { it.size == circles.size + 1 /*select all circle*/ }, ArgumentMatchers.anyInt())
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

      override fun prepareInjection() = super.prepareInjection().apply {
         registerProvider(FriendsListStorageDelegate::class.java,{ FriendsListStorageDelegate(friendInteractor,friendStorageInteractor, circleInteractor, profileInteractor)})
      }

      override fun mockActionService(): MockCommandActionService.Builder = super.mockActionService().apply {
         addContract(BaseContract.of(ActOnFriendRequestCommand.Accept::class.java).result(user))
         addContract(BaseContract.of(GetFriendsCommand::class.java).result(friends))
      }

      override fun getMainDescription() = "FriendListPresenter"
   }
}

