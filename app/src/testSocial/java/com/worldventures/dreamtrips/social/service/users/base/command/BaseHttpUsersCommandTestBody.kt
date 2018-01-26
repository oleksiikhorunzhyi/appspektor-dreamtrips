package com.worldventures.dreamtrips.social.service.users.base.command

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendCandidate
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.social.common.base.BaseTestBody
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber


abstract class BaseHttpUsersCommandTestBody<Command> : BaseTestBody {

   protected lateinit var interactor: FriendsInteractor

   open fun setup(actionService: MockHttpActionService) {
      val daggerActionService = mockDaggerActionService()
      val janet = mockJanetBuilder(listOf(mockCommandCacheWrapper(daggerActionService), actionService)).build()
      daggerActionService.registerProvider(Janet::class.java, { janet })
      interactor = mockInteractor(SessionActionPipeCreator(janet))
   }

   private fun mockInteractor(pipeCreator: SessionActionPipeCreator) = FriendsInteractor(pipeCreator)

   protected open fun mockJanetBuilder(actionServices: List<ActionService>) = Janet.Builder().apply {
      actionServices.forEach { addService(it) }
   }

   protected open fun mockUser(id: Int) = User().apply {
      firstName = "Name " + id.toString()
      lastName = "LastName " + id.toString()
      this.id = id
   }

   protected open fun mockFriendCandidate(number: Int): FriendCandidate {
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

   protected abstract fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService): CacheResultWrapper

   protected abstract fun mockDaggerActionService(): MockDaggerActionService

   protected open fun sendCommand(command: Command) = TestSubscriber<ActionState<Command>>()

   open fun mockHttpActionServiceBuidler() = MockHttpActionService.Builder()

}
