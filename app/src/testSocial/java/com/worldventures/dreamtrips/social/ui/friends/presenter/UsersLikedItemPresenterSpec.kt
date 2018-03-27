package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.service.users.liker.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.users.liker.delegate.LikersStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.bundle.UsersLikedEntityBundle
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class UsersLikedItemPresenterSpec : AbstractUserListPresenterSpec(UsersLikedItemTestSuite()) {

   class UsersLikedItemTestSuite : TestSuite<UsersLikedItemComponents>(UsersLikedItemComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Users Liked Item Presenter") {

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

                  it("Accept friend should notify view to show circle picker") {
                     presenter.acceptRequest(user)
                     verify(view).showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
                  }

                  it("Add friend should notify view to shoe circle picker") {
                     presenter.acceptRequest(user)
                     verify(view).showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
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

   class UsersLikedItemComponents : AbstractUserListComponents<UsersLikedItemPresenter, BaseUserListPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(UsersLikedItemPresenter(UsersLikedEntityBundle(mock(), 1)))
         view = mock()

         injector.registerProvider(LikersStorageDelegate::class.java, {
            LikersStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
         })
         injector.inject(presenter)
      }

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetLikersCommand::class.java).result(friends))
         }
      }
   }
}
