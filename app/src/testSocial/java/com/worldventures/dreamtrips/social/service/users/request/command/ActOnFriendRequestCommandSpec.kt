package com.worldventures.dreamtrips.social.service.users.request.command

import com.worldventures.core.janet.cache.CacheResultWrapper
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

class ActOnFriendRequestCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<ActOnFriendRequestCommand>() {

   private val user: User = mockUser(100500)
   private val FRIEND_CIRCLE_ID = "61bef157-12d8-41fd-a1cd-70643c099974"

   override fun create(): Spec.() -> Unit = {
      describe("Receive rejected user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should contains rejected user") {
            AssertUtil.assertActionSuccess(sendRejectCommand()) { (it.result?.id ?: false) == user.id }

         }
      }

      describe("Receive accepted user successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         it("Should contains accepted user") {
            AssertUtil.assertActionSuccess(sendAcceptCommand()) { (it.result?.id ?: false) == user.id }
         }
      }

      describe("Error during rejecting user") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendRejectCommand()) { it != null }
         }
      }

      describe("Error during accepting user") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendAcceptCommand(), { it != null })
         }
      }
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

   private fun sendAcceptCommand() = TestSubscriber<ActionState<ActOnFriendRequestCommand.Accept>>().apply {
      interactor.acceptRequestPipe
            .createObservable(ActOnFriendRequestCommand.Accept(user, FRIEND_CIRCLE_ID))
            .subscribe(this)
   }

   private fun sendRejectCommand() = TestSubscriber<ActionState<ActOnFriendRequestCommand.Reject>>().apply {
      interactor.rejectRequestPipe
            .createObservable(ActOnFriendRequestCommand.Reject(user))
            .subscribe(this)
   }
})
