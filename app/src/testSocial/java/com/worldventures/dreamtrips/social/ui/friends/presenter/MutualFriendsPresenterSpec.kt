package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.MutualFriendsStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.bundle.MutualFriendsBundle
import com.worldventures.dreamtrips.social.ui.friends.presenter.MutualFriendsPresenter.View
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class MutualFriendsPresenterSpec : AbstractUserListPresenterSpec(MutualFriendsPresenterTestBody()) {

   class MutualFriendsPresenterTestBody : AbstractUserListPresenterTestBody<View, MutualFriendsPresenter>() {
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

            it("Scrolling to last item must notify view new part of data") {
               presenter.takeView(view)
               presenter.scrolled(100, 100)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { friends.size == it.size })
            }

            it("Removing friend should notify view by data without it user") {
               presenter.takeView(view)
               presenter.unfriend(user)
               verify(view, VerificationModeFactory.times(2)).refreshUsers(argWhere { it.indexOf(user) == -1 })
            }
         }
      }

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())

      override fun mockPresenter(): MutualFriendsPresenter = spy(MutualFriendsPresenter(MutualFriendsBundle(100500)))

      override fun mockView(): View = mock()

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetMutualFriendsCommand::class.java).result(friends))
         }
      }

      override fun prepareInjection() = super.prepareInjection().apply {
         registerProvider(MutualFriendsStorageDelegate::class.java, {
            MutualFriendsStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
         })
      }

      override fun getMainDescription() = "MutualFriendsPresenterSpec"
   }
}
