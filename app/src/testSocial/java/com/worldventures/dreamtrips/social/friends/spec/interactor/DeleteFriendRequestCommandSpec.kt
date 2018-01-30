package com.worldventures.dreamtrips.social.friends.spec.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.friends.interactor.command.DeleteFriendRequestCommand
import com.worldventures.dreamtrips.social.service.friends.storage.RequestsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber


class DeleteFriendRequestCommandSpec : BaseSpec({
   describe("Receive hid user successfully") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Should send mocked query for hide user and return it") {
         it("Should return hid user entity") {
            AssertUtil.assertActionSuccess(sendCommand(DeleteFriendRequestCommand.Action.HIDE)) {
               (it.result?.id ?: false) == user.id
            }
         }
      }
   }

   describe("Error during hiding user") {
      beforeEachTest { setup(mockHttpServiceForError()) }

      context("Here must throw Exception") {
         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(DeleteFriendRequestCommand.Action.HIDE)) { it != null }
         }
      }
   }

   describe("Receive canceled user successfully") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Should send mocked query for cancel user and return it") {
         it("Should return canceled user entity") {
            AssertUtil.assertActionSuccess(sendCommand(DeleteFriendRequestCommand.Action.CANCEL)) {
               (it.result?.id ?: false) == user.id
            }
         }
      }
   }

   describe("Error during canceling user") {
      beforeEachTest { setup(mockHttpServiceForError()) }

      context("Here must throw Exception") {
         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(DeleteFriendRequestCommand.Action.CANCEL)) { it != null }
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
               .addService(commandCacheWrapper)
               .addService(httpService)
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200), {
                  it.url.contains("/api/social/friends/request_responses")
               })
               .build()
      }

      private fun mockHttpServiceForError(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422), {
                  it.url.contains("/api/social/friends/request_responses")
               })
               .build()
      }

      private fun sendCommand(action: DeleteFriendRequestCommand.Action)
            : TestSubscriber<ActionState<DeleteFriendRequestCommand>> {
         return TestSubscriber<ActionState<DeleteFriendRequestCommand>>().apply {
            friendsInteractor.deleteRequestPipe
                  .createObservable(DeleteFriendRequestCommand(user, action))
                  .subscribe(this)
         }
      }
   }
}