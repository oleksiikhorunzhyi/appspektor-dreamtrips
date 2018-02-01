package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.social.ui.friends.bundle.MutualFriendsBundle
import com.worldventures.dreamtrips.social.ui.friends.presenter.MutualFriendsPresenter.View
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetMutualFriendsCommand
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertTrue

class MutualFriendsPresenterSpec : AbstractUserListPresenterSpec(MutualFriendsPresenterTestBody()) {

   class MutualFriendsPresenterTestBody : AbstractUserListPresenterTestBody<View, MutualFriendsPresenter>() {
      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("View taken") {
            it("Should notify view with new user data") {
               verify(view).refreshUsers(argWhere { it.size == friends.size })
            }

            it("Should increment nextPage to 2") {
               assertTrue { presenter.nextPage == 2 }
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

      override fun getMainDescription() = "MutualFriendsPresenterSpec"
   }
}
