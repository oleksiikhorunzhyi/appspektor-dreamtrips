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
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendCandidate
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsStorageInteractor
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand
import com.worldventures.dreamtrips.social.service.users.friend.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.service.users.search.command.AddFriendCommand
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService

abstract class AbstractUserListPresenterSpec(testSuite: TestSuite<AbstractUserListComponents
<out BaseUserListPresenter<out BaseUserListPresenter.View>, out BaseUserListPresenter.View>>) : PresenterBaseSpec(testSuite) {

   abstract class AbstractUserListComponents<P : BaseUserListPresenter<V>, V : BaseUserListPresenter.View> : TestComponents<P, V>() {

      val user = mockUser(1)
      val circles = mockCircles()
      val friends = (1..getUsersPerPage()).map { mockFriendsCandidate(it) }.toList()

      protected lateinit var friendStorageInteractor: FriendsStorageInteractor
      protected lateinit var friendInteractor: FriendsInteractor
      protected lateinit var circleInteractor: CirclesInteractor
      protected lateinit var profileInteractor: ProfileInteractor

      fun init() {
         val janet = Janet.Builder().addService(mockActionService().build()).build()
         val pipeCreator = SessionActionPipeCreator(janet)
         val sessionHolder: SessionHolder = mockSessionHolder()

         friendInteractor = FriendsInteractor(pipeCreator)
         circleInteractor = CirclesInteractor(pipeCreator)
         profileInteractor = ProfileInteractor(pipeCreator, sessionHolder)
         friendStorageInteractor = FriendsStorageInteractor(SessionActionPipeCreator(janet))

         val injector = prepareInjector(sessionHolder).apply {
            registerProvider(FriendsInteractor::class.java, { friendInteractor })
            registerProvider(CirclesInteractor::class.java, { circleInteractor })
            registerProvider(ProfileInteractor::class.java, { profileInteractor })
            registerProvider(StartChatDelegate::class.java, { mock() })
         }

         onInit(injector, pipeCreator)
      }

      protected open fun mockActionService(): MockCommandActionService.Builder {
         return MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(BaseContract.of(RemoveFriendCommand::class.java).result(user))
            addContract(BaseContract.of(GetCirclesCommand::class.java).result(circles))
            addContract(BaseContract.of(AddFriendCommand::class.java).result(user))
         }
      }

      protected abstract fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator)

      private fun mockUser(userId: Int): User = User().apply {
         firstName = "Name"
         lastName = "LastName"
         id = userId
      }

      private fun mockCircles(): List<Circle> {
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

      private fun getUsersPerPage(): Int = 100
   }
}
