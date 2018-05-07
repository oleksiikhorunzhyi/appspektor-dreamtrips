package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argWhere
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.delegate.RequestsStorageDelegate
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class RequestPresenterSpec : PresenterBaseSpec(RequestTestSuite()) {

   class RequestTestSuite : TestSuite<RequestComponents>(RequestComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Request Presenter") {

               beforeEachTest {
                  init()
                  linkPresenterAndView()
               }

               describe("Refresh data") {
                  it("Presenter should reload data") {
                     verify(presenter).reloadRequests()
                  }

                  it("Should notify view with next part of data") {
                     presenter.loadNext()
                     val expectedCount = requests.size + EXPECTED_HEADERS_COUNT
                     verify(view, VerificationModeFactory.times(2)).itemsLoaded(argWhere {
                        it.size == expectedCount
                     }, any())
                  }
               }

               describe("Act on request") {
                  it("Should notify view to show circle picker after accept all") {
                     presenter.acceptAllRequests()
                     verify(view).showAddFriendDialog(argWhere { it.size == circles.size }, argWhere { true })
                  }

                  it("Should notify view to show circle picker after accept one") {
                     presenter.acceptRequest(user)
                     verify(view).showAddFriendDialog(argWhere { it.size == circles.size }, argWhere { true })
                  }

                  it("Should notify view to refresh data after reject request") {
                     presenter.rejectRequest(user)
                     verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
                  }

                  it("Should notify view to refresh data after cancel request") {
                     presenter.cancelRequest(user)
                     verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
                  }

                  it("Should notify view to refresh data after hide request") {
                     presenter.hideRequest(user)
                     verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
                  }
               }
            }
         }
      }
   }

   class RequestComponents : TestComponents<RequestsPresenter, RequestsPresenter.View>() {

      val EXPECTED_HEADERS_COUNT = 2
      private val USERS_PER_PAGE = 10

      val circles = mockCircles()
      val user = mockUser(100500)
      val requests = mockUsersList()

      fun init() {
         presenter = spy(RequestsPresenter())
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(BaseContract.of(GetRequestsCommand::class.java).result(requests))
            addContract(BaseContract.of(GetCirclesCommand::class.java).result(circles))
            addContract(BaseContract.of(AcceptAllFriendRequestsCommand::class.java))
            addContract(BaseContract.of(ActOnFriendRequestCommand.Accept::class.java).result(user))
            addContract(BaseContract.of(ActOnFriendRequestCommand.Reject::class.java).result(user))
            addContract(BaseContract.of(DeleteFriendRequestCommand::class.java).result(user))
         }.build()
         val janet = Janet.Builder().addService(service).build()

         val friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
         val fsInteractor = FriendsStorageInteractor(SessionActionPipeCreator(janet))
         val profileInteractor = ProfileInteractor(SessionActionPipeCreator(janet), mockSessionHolder())
         val circlesInteractor = CirclesInteractor(SessionActionPipeCreator(janet))

         whenever(context.getString(any())).thenReturn("title")

         prepareInjector().apply {
            registerProvider(FriendsInteractor::class.java) { friendsInteractor }
            registerProvider(CirclesInteractor::class.java) { circlesInteractor }
            registerProvider(ProfileInteractor::class.java) { profileInteractor }
            registerProvider(SnappyRepository::class.java) { mock() }
            registerProvider(RequestsStorageDelegate::class.java) {
               RequestsStorageDelegate(friendsInteractor, fsInteractor, circlesInteractor, profileInteractor)
            }
            inject(presenter)
         }
      }

      private fun mockCircles(): List<Circle> {
         val circleFriends = Circle.withTitle("Friends")
         circleFriends.id = "testFriendsCircleId"
         val circleCloseFriends = Circle.withTitle("Close friends")
         circleCloseFriends.id = "testCloseFriendsCircleId"
         return mutableListOf(circleFriends, circleCloseFriends)
      }

      private fun mockUser(userId: Int): User = User().apply {
         firstName = "Name"
         lastName = "LastName"
         id = userId
         relationship = if (userId % 5 == 0) User.Relationship.OUTGOING_REQUEST
         else User.Relationship.INCOMING_REQUEST
      }

      private fun mockUsersList(): List<User> {
         return (1..USERS_PER_PAGE).map {
            val user = mockUser(it)
            user.relationship = if (it % 2 == 0) User.Relationship.INCOMING_REQUEST
            else User.Relationship.OUTGOING_REQUEST
            user
         }.toList()
      }

      private fun mockSessionHolder(): SessionHolder {
         val sessionHolder: SessionHolder = mock()
         val userSession: UserSession = mock()
         val user: User = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         whenever(userSession.user()).thenReturn(user)
         return sessionHolder
      }
   }
}
