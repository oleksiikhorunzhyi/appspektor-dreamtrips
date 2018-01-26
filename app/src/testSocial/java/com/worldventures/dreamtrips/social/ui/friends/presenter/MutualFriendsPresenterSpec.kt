package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.service.users.friend.command.GetMutualFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.MutualFriendsStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.bundle.MutualFriendsBundle
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class MutualFriendsPresenterSpec : AbstractUserListPresenterSpec(MutualFriendsTestSuite()) {

   class MutualFriendsTestSuite : TestSuite<MutualFriendsComponents>(MutualFriendsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Mutual Friends Presenter") {

               describe("Refresh data") {

                  beforeEachTest {
                     init()
                     linkPresenterAndView()
                  }

                  it("Presenter should reload data") {
                     verify(presenter).reload()
                  }

                  it("View should receive new users data") {
                     verify(view).refreshUsers(argWhere { it.size == friends.size }, any())
                  }

                  it("Removing friend should notify view by data without it user") {
                     presenter.unfriend(user)
                     verify(view, VerificationModeFactory.times(2))
                           .refreshUsers(argWhere { it.indexOf(user) == -1 }, any())
                  }
               }
            }
         }
      }
   }

   class MutualFriendsComponents : AbstractUserListComponents<MutualFriendsPresenter, MutualFriendsPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(MutualFriendsPresenter(MutualFriendsBundle(100500)))
         view = mock()

         injector.apply {
            registerProvider(MutualFriendsStorageDelegate::class.java, {
               MutualFriendsStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
            })
            inject(presenter)
         }
      }

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetMutualFriendsCommand::class.java).result(friends))
         }
      }
   }
}
