package com.worldventures.dreamtrips.social.service.users.search.command

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

class AddFriendCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<AddFriendCommand>() {

   private val user: User = mockUser(100500)
   private val FRIEND_CIRCLE_ID = "61bef157-12d8-41fd-a1cd-70643c099974"

   override fun create(): Spec.() -> Unit = {
      describe("Receive added user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should return added user entity") {
            AssertUtil.assertActionSuccess(sendCommand()) {
               (it.result?.id ?: return@assertActionSuccess false) == user.id
            }
         }
      }

      describe("Error during adding  user") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(), { it != null })
         }
      }
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

   override fun mockDaggerActionService() = CommandActionService().wrapDagger()

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200), {
               it.url.contains("/api/social/friends/requests")
            })
            .build()
   }

   private fun mockHttpServiceForError(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(422), {
               it.url.contains("/api/social/friends/requests")
            })
            .build()
   }

   private fun sendCommand() = sendCommand(AddFriendCommand(user, FRIEND_CIRCLE_ID))

   override fun sendCommand(command: AddFriendCommand) = TestSubscriber<ActionState<AddFriendCommand>>().apply {
      interactor.addFriendPipe.createObservable(command).subscribe(this)
   }

})
