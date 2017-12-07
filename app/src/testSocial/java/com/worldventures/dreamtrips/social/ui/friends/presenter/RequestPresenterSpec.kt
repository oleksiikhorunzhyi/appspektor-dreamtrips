package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.test.common.Injector
import com.worldventures.core.ui.view.adapter.BaseArrayListAdapter
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.social.ui.friends.service.CirclesInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.command.*
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentMatchers
import org.mockito.internal.verification.VerificationModeFactory
import kotlin.test.assertTrue

class RequestPresenterSpec : AbstractPresenterSpec(RequestPresenterTestBody()) {

   class RequestPresenterTestBody : TestBody<RequestsPresenter.View, RequestsPresenter> {
      private lateinit var presenter: RequestsPresenter
      private lateinit var view: RequestsPresenter.View
      private lateinit var friendsInteractor: FriendsInteractor
      private lateinit var circlesInteractor: CirclesInteractor
      private val snappyRepository: SnappyRepository = mock()
      private val circles = mockCircles()
      private val user = mockUser(100500)
      private val adapter: BaseArrayListAdapter<Any> = mock()
      private val requests = mockUsersList()

      private fun createTestSuit(): SpecBody.() -> Unit = {
         describe("View taken") {
            it("Should subscribe on get request result") {
               verify(presenter).observeRequests()
            }

            it("Should invoke reload request") {
               verify(presenter).reloadRequests()
            }

            it("Should notify view with outgoing and incoming requests + two headers") {
               verify(view).itemsLoaded(argWhere<List<Any>> { it.size == (requests.size + EXPECTED_HEADERS_COUNT) }
                     , ArgumentMatchers.anyBoolean())
            }
         }

         describe("Accept all requests") {
            it("Should invoke getCircleObservable()") {
               presenter.acceptAllRequests()
               verify(presenter).circlesObservable
            }

            it("Should notify view with request data with relationship != INCOMING_REQUEST") {
               presenter.allFriendRequestsAccepted()
               verify(view, VerificationModeFactory.atLeastOnce())
                     .itemsLoaded(argWhere<List<Any>> {
                        it.none { (it is User) && it.relationship == User.Relationship.INCOMING_REQUEST }
                     }, ArgumentMatchers.anyBoolean())
            }
         }

         describe("Page counter") {
            it("Should increment currentPage counter to 2") {
               assertTrue { presenter.currentPage == 2 }
            }

            it("Should increment currentPage to 3") {
               presenter.loadNext()
               assertTrue { presenter.currentPage == 3 }
            }

            it("Should refresh currentPage") {
               presenter.loadNext()
               presenter.reloadRequests()
               assertTrue { presenter.currentPage == 2 }
            }
         }

         describe("Reject request") {
            it("Should invoke rejectRequestPipe and remove rejected user from adapter") {
               presenter.rejectRequest(user)
               verify(friendsInteractor).rejectRequestPipe()
               verify(adapter).remove(argWhere<Any> { (it as User) == user })
            }
         }

         describe("Hide/Cancel request") {
            it("Should invoke delete fun with DeleteFriendRequestCommand.Action.HIDE") {
               presenter.hideRequest(user)
               verify(presenter).deleteRequest(argWhere { it == user }
                     , argWhere { it == DeleteFriendRequestCommand.Action.HIDE })
            }

            it("Should invoke delete fun with DeleteFriendRequestCommand.Action.CANCEL") {
               presenter.cancelRequest(user)
               verify(presenter).deleteRequest(argWhere { it == user }
                     , argWhere { it == DeleteFriendRequestCommand.Action.CANCEL })
            }
         }

         describe("Delete request") {
            it("Should remove user from adapter") {
               presenter.deleteRequest(user, DeleteFriendRequestCommand.Action.CANCEL)
               verify(adapter, atLeastOnce()).remove(argWhere<Any> { it == user })
            }
         }

         describe("User clicked") {
            it("Should notify view to open user inform") {
               presenter.userClicked(user)
               verify(view).openUser(argWhere { it.user == user })
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

         friendsInteractor = spy(FriendsInteractor(SessionActionPipeCreator(janet)))
         circlesInteractor = spy(CirclesInteractor(SessionActionPipeCreator(janet)))

         whenever(view.adapter).thenReturn(adapter)
         whenever(adapter.items).thenReturn(requests)

         prepareInjection(presenter)
         presenter.takeView(view)
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
      }

      private fun mockUsersList(): List<User> = (1..USERS_PER_PAGE).map {
         val user = mockUser(it)
         if (it % 2 == 0) user.relationship = User.Relationship.INCOMING_REQUEST
         else user.relationship = User.Relationship.OUTGOING_REQUEST
         user
      }.toList()

      override fun prepareInjection(presenter: RequestsPresenter): Injector = prepareInjector().apply {
         registerProvider(FriendsInteractor::class.java) { friendsInteractor }
         registerProvider(CirclesInteractor::class.java) { circlesInteractor }
         registerProvider(SnappyRepository::class.java) { snappyRepository }
         inject(presenter)
      }

      companion object {
         private const val USERS_PER_PAGE = 10
         private const val EXPECTED_HEADERS_COUNT = 2
      }
   }
}