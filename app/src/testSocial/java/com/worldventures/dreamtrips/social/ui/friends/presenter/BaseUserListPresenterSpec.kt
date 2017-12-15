package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.dreamtrips.social.ui.friends.presenter.BaseUserListPresenter.View
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertTrue

class BaseUserListPresenterSpec : AbstractUserListPresenterSpec(BaseUserListPresenterTestBody()) {

   class BaseUserListPresenterTestBody : AbstractUserListPresenterTestBody<View
         , BaseUserListPresenter<View>>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("View taken") {
            it("Should call invoke func to preload data") {
               verify(presenter).reload()
            }
         }

         describe("reload()") {
            it("Should notify view that loading  data has begun") {
               verify(view).startLoading()
            }

            it("Should invoke subscribeToRemovedFriends") {
               verify(presenter, VerificationModeFactory.atLeastOnce()).subscribeToRemovedFriends()
            }

            it("Should invoke subscribeToChangingCircles") {
               verify(presenter, VerificationModeFactory.atLeastOnce()).subscribeToChangingCircles()
            }
         }

         describe("scrolled") {
            it("Should pass all checks and notify view that loading data has begun") {
               presenter.scrolled(friends.size, friends.size - 1)
               verify(view, VerificationModeFactory.times(2)).startLoading()
            }
         }

         describe("refresh users") {
            it("Should notify view about new portion of data and increment page to 2") {
               assertTrue { presenter.nextPage == 1 }
               presenter.processNewUsers(users)
               verify(view).refreshUsers(argWhere { it.size == friends.size })
               verify(view, VerificationModeFactory.atLeastOnce()).finishLoading()
               assertTrue { presenter.nextPage == 2 }
            }
         }

         describe("openPrefs()") {
            it("Should notify view that it should open user preferences") {
               presenter.openPrefs(user)
               verify(view).openFriendPrefs(argWhere { (it.user?.id ?: false) == user.id })
            }
         }

         describe("startChat()") {
            it("Should delegate user to start chat") {
               presenter.startChat(user)
               verify(startChatDelegate).startSingleChat(argWhere<User> { it.id == user.id }, argWhere { true })
            }
         }

         describe("Remove user from friends") {
            it("Should invoke userActionSucceed() and change user relationships to NONE") {
               presenter.unfriend(user)
               verify(presenter).userActionSucceed(argWhere { it.relationship == User.Relationship.NONE })
            }

            it("Should notify view that data have already finished loading") {
               presenter.unfriend(user)
               verify(view, VerificationModeFactory.atLeastOnce()).finishLoading()
            }
         }

         describe("Add user as a friend") {
            it("Should invoke getCirclesObservable() and notify view for show add friend dialog") {
               presenter.addFriend(user)
               verify(presenter).circlesObservable
               verify(view)
                     .showAddFriendDialog(argWhere<List<Circle>> { it.size == circles.size }, argWhere { true })
            }

            it("Should change adding user status to OUTGOING_REQUEST") {
               presenter.addFriend(user, circles[0])
               assertTrue { user.relationship == User.Relationship.OUTGOING_REQUEST }
            }
         }
      }

      override fun mockPresenter(): BaseUserListPresenter<View> = spy()

      override fun mockView(): View = mock()

      override fun getMainDescription() = "BaseUserListPresenter"

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())
   }
}