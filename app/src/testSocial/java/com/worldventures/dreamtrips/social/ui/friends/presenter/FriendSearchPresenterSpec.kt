package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.service.users.search.command.GetSearchUsersCommand
import com.worldventures.dreamtrips.social.service.users.search.delegate.SearchedUsersStorageDelegate
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class FriendSearchPresenterSpec : AbstractUserListPresenterSpec(FriendSearchTestSuite()) {

   class FriendSearchTestSuite : TestSuite<FriendSearchComponents>(FriendSearchComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Friend Search Presenter") {

               describe("Refresh data") {

                  beforeEachTest {
                     init()
                     linkPresenterAndView()
                  }

                  it("Search should notify view with new user data") {
                     val query = "friend name"
                     presenter.search(query)
                     verify(view).refreshUsers(argWhere { it.size == friends.size }, any())
                  }

                  it("Empty query should notify view with empty list of data") {
                     val query = ""
                     presenter.search(query)
                     verify(view).refreshUsers(argWhere { it.isEmpty() }, any())
                  }

                  it("Should notify view open circle selector alert") {
                     presenter.addUserRequest(user)
                     verify(view, VerificationModeFactory.atLeastOnce())
                           .showAddFriendDialog(argWhere { circles.size == it.size }, argWhere { true })
                  }
               }
            }
         }
      }
   }

   class FriendSearchComponents : AbstractUserListComponents<FriendSearchPresenter, FriendSearchPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(FriendSearchPresenter(""))
         view = mock()

         injector.apply {
            registerProvider(SearchedUsersStorageDelegate::class.java, {
               SearchedUsersStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
            })
            inject(presenter)
         }
      }

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService().apply {
            addContract(BaseContract.of(GetSearchUsersCommand::class.java).result(friends))
         }
      }
   }
}
