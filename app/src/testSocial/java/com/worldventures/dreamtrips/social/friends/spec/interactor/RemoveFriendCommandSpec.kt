package com.worldventures.dreamtrips.social.friends.spec.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.command.RemoveFriendCommand
import com.worldventures.dreamtrips.social.ui.friends.storage.RequestsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber


class RemoveFriendCommandSpec : BaseSpec({
   describe("Receive removed user successfully") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Should send mocked query for removing user and return it") {
         it("Should return removed user entity") {
            AssertUtil.assertActionSuccess(sendCommand()) { (it.result?.id ?: false) == user.id }
         }
      }
   }

   describe("Error during removing friend") {
      beforeEachTest { setup(mockHttpServiceForError()) }

      context("Here must throw Exception") {
         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand()) { it != null }
         }
      }
   }
}) {
   companion object {
      private lateinit var friendsInteractor: FriendsInteractor
      private val user: User = MockUtil.mockUser(100500)

      private fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()
         val storage = RequestsStorage()
         val commandCacheWrapper = CacheResultWrapper(daggerCommandActionService)
         commandCacheWrapper.bindStorage(storage.actionClass, storage)

         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService)
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))

      }

      private fun mockHttpServiceForSuccessResult(): MockHttpActionService = MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200), {
               it.url.contains("api/social/friends/")
            }).build()

      private fun mockHttpServiceForError(): MockHttpActionService = MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(422), {
               it.url.contains("api/social/friends/")
            }).build()

      private fun sendCommand(): TestSubscriber<ActionState<RemoveFriendCommand>> {
         return TestSubscriber<ActionState<RemoveFriendCommand>>().apply {
            friendsInteractor.removeFriendPipe()
                  .createObservable(RemoveFriendCommand(user))
                  .subscribe(this)
         }
      }
   }
}