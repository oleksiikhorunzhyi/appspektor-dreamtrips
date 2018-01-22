package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.users.liker.delegate.LikersStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle
import com.worldventures.dreamtrips.social.ui.friends.presenter.BaseUserListPresenter.View
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class UsersLikedItemPresenterSpec : AbstractUserListPresenterSpec(UsersLikedItemPresenterTestBody()) {

   class UsersLikedItemPresenterTestBody : AbstractUserListPresenterTestBody<View, UsersLikedItemPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("Refresh data") {
            it("Presenter should reload data") {
               presenter.takeView(view)
               verify(presenter).reload()
            }

            it("View should receive new users data") {
               presenter.takeView(view)
               verify(view).refreshUsers(argWhere { it.size == friends.size }, any())
            }

            it("Accept friend should notify view to show circle picker") {
               presenter.takeView(view)
               presenter.acceptRequest(user)
               verify(view).showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
            }

            it("Add friend should notify view to shoe circle picker") {
               presenter.takeView(view)
               presenter.acceptRequest(user)
               verify(view).showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
            }

            it("Removing friend should notify view by data without it user") {
               presenter.takeView(view)
               presenter.unfriend(user)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { it.indexOf(user) == -1 }, any())
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

      override fun prepareInjection() = super.prepareInjection().apply {
         registerProvider(LikersStorageDelegate::class.java, {
            LikersStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
         })
      }

      override fun getMainDescription() = "UsersLikedItemPresenter"
   }
}
