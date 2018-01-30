package com.worldventures.dreamtrips.social.friends.spec.interactor

import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.janet.cache.CacheResultWrapper
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.friends.interactor.command.AcceptAllFriendRequestsCommand
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

class AcceptAllFriendRequestsCommandSpec : BaseSpec({
   describe("Execute accept all event successfully") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Event should execute correctly") {
         it("Should accept all users and return null as result") {
            AssertUtil.assertActionSuccess(sendCommand()) { it.result == null }
         }
      }
   }

   describe("Error during receiving  accepting all users") {
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
      private const val FRIEND_CIRCLE_ID = "61bef157-12d8-41fd-a1cd-70643c099974"

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
                  it.url.contains("api/social/friends/request_responses/accept_all")
               })
               .build()
      }

      private fun mockHttpServiceForError(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422), {
                  it.url.contains("api/social/friends/request_responses/accept_all")
               })
               .build()
      }

      private fun sendCommand(): TestSubscriber<ActionState<AcceptAllFriendRequestsCommand>> {
        return TestSubscriber<ActionState<AcceptAllFriendRequestsCommand>>().apply {
         friendsInteractor.acceptAllPipe
               .createObservable(AcceptAllFriendRequestsCommand(FRIEND_CIRCLE_ID))
               .subscribe(this)
        }
      }
   }
}