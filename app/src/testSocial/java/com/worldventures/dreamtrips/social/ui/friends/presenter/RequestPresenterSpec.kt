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
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand
import com.worldventures.dreamtrips.social.service.users.request.command.AcceptAllFriendRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.users.request.command.GetRequestsCommand
import com.worldventures.dreamtrips.social.service.users.request.delegate.RequestsStorageDelegate
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class RequestPresenterSpec : AbstractPresenterSpec(RequestPresenterTestBody()) {

   class RequestPresenterTestBody : TestBody<RequestsPresenter.View, RequestsPresenter> {
      private lateinit var presenter: RequestsPresenter
      private lateinit var view: RequestsPresenter.View
      private lateinit var friendsInteractor: FriendsInteractor
      private lateinit var fsInteractor: FriendsStorageInteractor
      private lateinit var profileInteractor: ProfileInteractor
      private lateinit var circlesInteractor: CirclesInteractor
      private val snappyRepository: SnappyRepository = mock()
      private val circles = mockCircles()
      private val user = mockUser(100500)
      private val requests = mockUsersList()

      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("Refresh data") {
            it("Presenter should reload data") {
               presenter.takeView(view)
               verify(presenter).reloadRequests()
            }

            it("Should notify view with next part of data") {
               presenter.takeView(view)
               presenter.loadNext()
               val expectedCount = requests.size + EXPECTED_HEADERS_COUNT
               verify(view, VerificationModeFactory.times(2)).itemsLoaded(argWhere {
                  it.size == expectedCount
               }, any())
            }
         }

         describe("Act on request") {
            it("Should notify view to show circle picker after accept all") {
               presenter.takeView(view)
               presenter.acceptAllRequests()
               verify(view).showAddFriendDialog(argWhere { it.size == circles.size }, argWhere { true })
            }

            it("Should notify view to show circle picker after accept one") {
               presenter.takeView(view)
               presenter.acceptRequest(user)
               verify(view).showAddFriendDialog(argWhere { it.size == circles.size }, argWhere { true })
            }

            it("Should notify view to refresh data after reject request") {
               presenter.takeView(view)
               presenter.rejectRequest(user)
               verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
            }

            it("Should notify view to refresh data after cancel request") {
               presenter.takeView(view)
               presenter.cancelRequest(user)
               verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
            }

            it("Should notify view to refresh data after hide request") {
               presenter.takeView(view)
               presenter.hideRequest(user)
               verify(view, VerificationModeFactory.times(2)).itemsLoaded(any(), any())
            }
         }
      }

      override fun createTestBody(): Spec.() -> Unit = {
         describe("RequestPresenterTestBody", {
            beforeEachTest { init() }
            createTestSuits().forEach { it.invoke(this) }
         })
      }

      override fun createTestSuits(): List<SpecBody.() -> Unit> = listOf(createTestSuit())

      override fun init() {
         presenter = mockPresenter()
         view = mockView()

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

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
         fsInteractor = FriendsStorageInteractor(SessionActionPipeCreator(janet))
         profileInteractor = ProfileInteractor(SessionActionPipeCreator(janet), mockSessionHolder())
         circlesInteractor = CirclesInteractor(SessionActionPipeCreator(janet))

         whenever(context.getString(any())).thenReturn("title")

         prepareInjection()
      }

      override fun mockPresenter(): RequestsPresenter = spy(RequestsPresenter())

      override fun mockView(): RequestsPresenter.View = mock()

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

      override fun prepareInjection(): Injector {
         return prepareInjector().apply {
            registerProvider(FriendsInteractor::class.java) { friendsInteractor }
            registerProvider(CirclesInteractor::class.java) { circlesInteractor }
            registerProvider(ProfileInteractor::class.java) { profileInteractor }
            registerProvider(SnappyRepository::class.java) { snappyRepository }
            registerProvider(RequestsStorageDelegate::class.java) {
               RequestsStorageDelegate(friendsInteractor, fsInteractor, circlesInteractor, profileInteractor)
            }
            inject(presenter)
         }
      }

      private fun mockSessionHolder(): SessionHolder {
         val sessionHolder: SessionHolder = mock()
         val userSession: UserSession = mock()
         val user: User = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         whenever(userSession.user()).thenReturn(user)
         return sessionHolder
      }

      companion object {
         private const val USERS_PER_PAGE = 10
         private const val EXPECTED_HEADERS_COUNT = 2
      }
   }
}
