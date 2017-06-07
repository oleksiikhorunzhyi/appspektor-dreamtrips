package com.worldventures.dreamtrips.social.friends

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.modules.friends.service.command.GetRequestsCommand
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xdescribe
import rx.observers.TestSubscriber

class RequestsInteractorSpec : BaseSpec({
   // TODO this spec is just a stub, temporarily ignore it
   // until actual implementation is written
   xdescribe("Getting requests list") {
      context("Refresh requests") {
         it("Cache is empty") {
            val testSubscriber = TestSubscriber<ActionState<GetRequestsCommand>>()

            friendsInteractor.requestsPipe
                  .createObservable(GetRequestsCommand(1))
                  .subscribe(testSubscriber)
         }
      }
   }
}) {
   companion object {
      lateinit var friendsInteractor: FriendsInteractor

      val apiUsers: List<FriendCandidate> = mock()

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()

         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService)
               .build()

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      fun mockHttpServiceForRequests(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiUsers)) { it.url.contains("/api/social/friends/requests") }
               .build()
      }

      fun mockHttpServiceForError(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422)) { it.url.contains("/api/social/friends/requests") }
               .build()
      }
   }
}