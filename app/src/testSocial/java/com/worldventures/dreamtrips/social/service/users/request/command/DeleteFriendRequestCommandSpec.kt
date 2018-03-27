package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.command.BaseHttpUsersCommandTestBody
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class DeleteFriendRequestCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<DeleteFriendRequestCommand>() {

   private val user: User = mockUser(100500)

   override fun create(): Spec.() -> Unit = {
      describe("Receive hid user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should return hid user entity") {
            AssertUtil.assertActionSuccess(sendCommand(DeleteFriendRequestCommand.Action.HIDE)) {
               (it.result?.id ?: false) == user.id
            }
         }

      }

      describe("Receive canceled user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should return canceled user entity") {
            AssertUtil.assertActionSuccess(sendCommand(DeleteFriendRequestCommand.Action.CANCEL)) {
               (it.result?.id ?: false) == user.id
            }
         }
      }

      describe("Error during hiding user") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(DeleteFriendRequestCommand.Action.HIDE)) { it != null }
         }
      }

      describe("Error during canceling user") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(DeleteFriendRequestCommand.Action.CANCEL)) { it != null }
         }
      }
   }

   private fun sendCommand(action: DeleteFriendRequestCommand.Action) = sendCommand(DeleteFriendRequestCommand(user, action))

   override fun sendCommand(command: DeleteFriendRequestCommand) = TestSubscriber<ActionState<DeleteFriendRequestCommand>>().apply {
      interactor.deleteRequestPipe.createObservable(command).subscribe(this)
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

   override fun mockDaggerActionService() = CommandActionService().wrapDagger()

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
})

