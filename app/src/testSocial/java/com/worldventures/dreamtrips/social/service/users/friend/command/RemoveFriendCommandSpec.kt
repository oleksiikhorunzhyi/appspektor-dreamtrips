package com.worldventures.dreamtrips.social.service.users.friend.command

import com.worldventures.janet.cache.CacheResultWrapper
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

class RemoveFriendCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<RemoveFriendCommand>() {

   private val user = mockUser(100500)

   override fun create(): Spec.() -> Unit = {
      describe("Receive removed user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should return removed user entity") {
            AssertUtil.assertActionSuccess(sendCommand(RemoveFriendCommand(user))) {
               (it.result?.id ?: false) == user.id
            }
         }
      }

      describe("Error during removing friend") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(RemoveFriendCommand(user))) { it != null }
         }
      }
   }

   override fun sendCommand(command: RemoveFriendCommand) = TestSubscriber<ActionState<RemoveFriendCommand>>().apply {
      interactor.removeFriendPipe.createObservable(command).subscribe(this)
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

   override fun mockDaggerActionService() = CommandActionService().wrapDagger()

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(200), {
            it.url.contains("api/social/friends/")
         }).build()

   private fun mockHttpServiceForError(): MockHttpActionService = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(422), {
            it.url.contains("api/social/friends/")
         }).build()
})
