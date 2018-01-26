package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.janet.cache.CacheResultWrapper
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

class AcceptAllFriendRequestsCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<AcceptAllFriendRequestsCommand>() {

   private val FRIEND_CIRCLE_ID = "61bef157-12d8-41fd-a1cd-70643c099974"

   override fun create(): Spec.() -> Unit = {
      describe("Execute accept all event successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should accept all users and return null as result") {
            AssertUtil.assertActionSuccess(sendCommand()) { it.result == null }
         }
      }

      describe("Error during receiving  accepting all users") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand()) { it != null }
         }
      }
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

   override fun mockDaggerActionService() = CommandActionService().wrapDagger()

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200)) {
               it.url.contains("api/social/friends/request_responses/accept_all")
            }.build()
   }

   private fun mockHttpServiceForError(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(422), {
               it.url.contains("api/social/friends/request_responses/accept_all")
            })
            .build()
   }

   private fun sendCommand() = sendCommand(AcceptAllFriendRequestsCommand(FRIEND_CIRCLE_ID))

   override fun sendCommand(command: AcceptAllFriendRequestsCommand) = TestSubscriber<ActionState<AcceptAllFriendRequestsCommand>>().apply {
      interactor.acceptAllPipe.createObservable(command).subscribe(this)
   }
})
