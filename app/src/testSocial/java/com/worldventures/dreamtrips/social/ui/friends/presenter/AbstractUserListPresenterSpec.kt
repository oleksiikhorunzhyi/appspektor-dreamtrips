package com.worldventures.dreamtrips.social.ui.friends.presenter

import com.messenger.delegate.StartChatDelegate
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Circle
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendCandidate
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import com.worldventures.dreamtrips.social.ui.friends.presenter.AbstractPresenterSpec.TestBody
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe

@Suppress("LeakingThis")
abstract class AbstractUserListPresenterSpec(testBody: TestBody<*, *>) : PresenterBaseSpec(testBody.createTestBody()) {

   abstract class AbstractUserListPresenterTestBody<View : BaseUserListPresenter.View, Presenter : BaseUserListPresenter<View>>
      : TestBody<View, Presenter> {
      protected lateinit var presenter: Presenter
      protected lateinit var view: View
      protected lateinit var friendStorageInteractor: FriendsStorageInteractor
      protected lateinit var friendInteractor: FriendsInteractor
      protected lateinit var circleInteractor: CirclesInteractor
      protected lateinit var profileInteractor: ProfileInteractor
      protected val startChatDelegate: StartChatDelegate = mock()
      protected val user = mockUser(1)
      protected val circles = mockCircles()
      protected val friends = (1..getUsersPerPage()).map { mockFriendsCandidate(it) }.toList()
      protected val users = (1..getUsersPerPage()).map { mockUser(it) }.toList()
      protected val sessionHolder: SessionHolder = mockSessionHolder()

      override fun createTestBody(): Spec.() -> Unit = {
         describe(getMainDescription()) {
            beforeEachTest { init() }
            createTestSuits().forEach { it.invoke(this) }
         }
      }

      override fun init() {
         presenter = mockPresenter()
         view = mockView()
         mockInteractors(Janet.Builder().addService(mockActionService().build()).build())
         prepareInjection().inject(presenter)
      }

      override fun prepareInjection() = PresenterBaseSpec.prepareInjector(sessionHolder).apply {
         registerProvider(FriendsInteractor::class.java, { friendInteractor })
         registerProvider(CirclesInteractor::class.java, { circleInteractor })
         registerProvider(ProfileInteractor::class.java, { profileInteractor })
         registerProvider(StartChatDelegate::class.java, { startChatDelegate })
      }

      protected abstract fun getMainDescription(): String

      override abstract fun createTestSuits(): List<SpecBody.() -> Unit>

      override abstract fun mockPresenter(): Presenter

      override abstract fun mockView(): View

      protected open fun mockActionService(): MockCommandActionService.Builder {
         return MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(BaseContract.of(RemoveFriendCommand::class.java).result(user))
            addContract(BaseContract.of(GetCirclesCommand::class.java).result(circles))
            addContract(BaseContract.of(AddFriendCommand::class.java).result(user))
         }
      }

      protected open fun mockInteractors(janet: Janet) {
         friendInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
         circleInteractor = CirclesInteractor(SessionActionPipeCreator(janet))
         profileInteractor = ProfileInteractor(SessionActionPipeCreator(janet), sessionHolder)
         friendStorageInteractor = FriendsStorageInteractor(SessionActionPipeCreator(janet))
      }

      protected open fun mockUser(userId: Int): User = User().apply {
         firstName = "Name"
         lastName = "LastName"
         id = userId
      }

      protected open fun mockCircles(): List<Circle> {
         val circleFriends = Circle.withTitle("Friends")
         circleFriends.id = "testFriendsCircleId"
         val circleCloseFriends = Circle.withTitle("Close friends")
         circleCloseFriends.id = "testCloseFriendsCircleId"
         return mutableListOf(circleFriends, circleCloseFriends)
      }

      protected open fun mockSessionHolder(): SessionHolder {
         val sessionHolder: SessionHolder = mock()
         val userSession: UserSession = mock()
         val user: User = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         whenever(userSession.user()).thenReturn(user)
         return sessionHolder
      }

      protected open fun mockFriendsCandidate(number: Int): FriendCandidate {
         val avatar = ImmutableAvatar.builder()
               .medium("test")
               .original("test")
               .thumb("test")
               .build()

         return ImmutableFriendCandidate.builder()
               .id(number)
               .username(number.toString())
               .firstName("first_name")
               .lastName("last_name")
               .avatar(avatar)
               .location("test")
               .badges(ArrayList())
               .build()
      }

      protected open fun getUsersPerPage(): Int = 100
   }
}
