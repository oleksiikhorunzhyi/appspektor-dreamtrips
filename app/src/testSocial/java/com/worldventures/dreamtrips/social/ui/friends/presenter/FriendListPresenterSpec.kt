package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.service.users.friend.delegate.FriendsListStorageDelegate
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertTrue

class FriendListPresenterSpec : AbstractUserListPresenterSpec(FriendListTestSuite()) {

   class FriendListTestSuite : TestSuite<FriendListComponents>(FriendListComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Friend List Presenter") {

               beforeEachTest {
                  init()
                  linkPresenterAndView()
               }

               describe("Refresh data") {
                  it("Presenter should reload data") {
                     verify(presenter).reload()
                  }

                  it("View should receive new users data") {
                     verify(view).refreshUsers(argWhere { it.size == friends.size }, any())
                  }

                  it("Apply filters should notify view with new user data and contains input args") {
                     presenter.reloadWithFilter(circles[0], 1)
                     verify(view, VerificationModeFactory.times(2))
                           .refreshUsers(argWhere { it.size == friends.size }, any())
                     assertTrue { presenter.selectedCircle?.id == circles[0].id }
                     assertTrue { presenter.position == 1 }
                  }

                  it("Search should notify view with new user data") {
                     val query = "friend name"
                     presenter.search(query)
                     verify(view, VerificationModeFactory.times(2))
                           .refreshUsers(argWhere { it.size == friends.size }, any())
                  }

                  it("Empty query shouldn't initiate receive new part of data") {
                     val query = ""
                     presenter.search(query)
                     verify(view, VerificationModeFactory.times(1))
                           .refreshUsers(argWhere { it.size == friends.size }, any())
                  }

                  it("Removing friend should notify view by data without it user") {
                     presenter.unfriend(user)
                     verify(view, VerificationModeFactory.times(2))
                           .refreshUsers(argWhere { it.indexOf(user) == -1 }, any())
                  }
               }

               describe("Show filters") {
                  it("Presenter should notify view to open filters") {
                     presenter.onFilterClicked()
                     verify(view).showFilters(argWhere { it.size == circles.size + 1 /*select all circle*/ },
                           ArgumentMatchers.anyInt())
                  }
               }
            }
         }
      }
   }

   class FriendListComponents : AbstractUserListComponents<FriendListPresenter, FriendListPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(FriendListPresenter())
         view = mock()

         injector.apply {
            registerProvider(FriendsListStorageDelegate::class.java, {
               FriendsListStorageDelegate(friendInteractor, friendStorageInteractor, circleInteractor, profileInteractor)
            })
            inject(presenter)
         }
      }

      override fun mockActionService(): MockCommandActionService.Builder = super.mockActionService().apply {
         addContract(BaseContract.of(ActOnFriendRequestCommand.Accept::class.java).result(user))
         addContract(BaseContract.of(GetFriendsCommand::class.java).result(friends))
      }
   }
}
