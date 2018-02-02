package com.worldventures.dreamtrips.social.service.users.request.command

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.command.BaseHttpUsersCommandTestBody
import com.worldventures.dreamtrips.social.service.users.request.storage.RequestsStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetRequestsCommandSpeck : BaseBodySpec(object : BaseHttpUsersCommandTestBody<GetRequestsCommand>() {

   private val PAGE_SIZE = 100
   private val apiUsersPage1: List<FriendCandidate> = (1..PAGE_SIZE).map { it -> mockFriendCandidate(it) }.toList()
   private val apiUsersPage2: List<FriendCandidate> = listOf(mockFriendCandidate(PAGE_SIZE + 1))

   override fun create(): Spec.() -> Unit = {
      describe("Receive request users successfully") {
         setup(mockHttpServiceForSuccessResult())

         describe("First page is received and processed correctly") {
            val firstPageRequest = GetRequestsCommand(1)
            val testSubscriber = sendCommand(firstPageRequest)

            it("should indicate that it is first page and errors didn't occur") {
               AssertUtil.assertActionSuccess(testSubscriber, { firstPageRequest.isFirstPage })
            }

            it("should contain 100 received and converted entities") {
               assertTrue { firstPageRequest.items().size == apiUsersPage1.size }
               assertTrue { firstPageRequest.items().filter { it.id != 0 }.isEmpty() }
            }

            it("should inform that there is at least one more page") {
               assertFalse { firstPageRequest.isNoMoreElements }
            }
         }

         describe("Pagination should proceed for second page as well") {
            val secondPageRequest = GetRequestsCommand(2)
            val testSubscriber = sendCommand(secondPageRequest)

            it("should indicate that it isn't first page and errors didn't occur") {
               AssertUtil.assertActionSuccess(testSubscriber, { !secondPageRequest.isFirstPage })
            }

            it("should contain 1 received and converted entity and 100 elements from previous page") {
               assertTrue { secondPageRequest.items().size == apiUsersPage1.size + apiUsersPage2.size }
               assertTrue { secondPageRequest.items().filter { it.id != 0 }.isEmpty() }
            }

            it("should inform that there is at least one more page") {
               assertFalse { secondPageRequest.isNoMoreElements }
            }
         }

         describe("Pagination should stop, because there is no more elements") {
            val thirdPageRequest = GetRequestsCommand(3)
            val testSubscriber = sendCommand(thirdPageRequest)

            it("should indicate that it isn't first page and errors didn't occur") {
               AssertUtil.assertActionSuccess(testSubscriber) { !thirdPageRequest.isFirstPage }
            }

            it("should contain only 101 elements from cache") {
               assertTrue { thirdPageRequest.items().size == apiUsersPage1.size + apiUsersPage2.size }
               assertTrue { thirdPageRequest.items().filter { it.id != 0 }.isEmpty() }
            }

            it("should inform that there is no more pages") {
               assertTrue { thirdPageRequest.isNoMoreElements }
            }
         }
      }

      describe("Error during receiving request users") {
         setup(mockHttpServiceForError())
         val firstPageRequest = GetRequestsCommand(1)
         val testSubscriber = sendCommand(firstPageRequest)

         it("exception should be thrown and result should be empty") {
            AssertUtil.assertActionFail(testSubscriber, { firstPageRequest.items().isEmpty() })
         }
      }

   }

   override fun sendCommand(command: GetRequestsCommand) = TestSubscriber<ActionState<GetRequestsCommand>>().apply {
      interactor.requestsPipe.createObservable(command).subscribe(this)
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)
         .bindStorage(RequestsStorage().actionClass, RequestsStorage())

   override fun mockDaggerActionService() = CommandActionService().wrapDagger().apply {
      val mapperyContext = mock<MapperyContext>()
      whenever(mapperyContext.convert(any(), eq(User::class.java))).thenAnswer {
         if (it.arguments[0] is List<*>)
            (it.arguments[0] as List<*>).map { any ->
               mock<User>().apply { id = (any as FriendCandidate).id() }
            }.toList()
         else ArrayList<FriendCandidate>()
      }
      registerProvider(MapperyContext::class.java, { mapperyContext })
   }

   fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(MockHttpActionService.Response(200).body(apiUsersPage1)) {
               it.url.contains("/api/social/friends/requests") && it.url.contains("?page=1")
            }
            .bind(MockHttpActionService.Response(200).body(apiUsersPage2)) {
               it.url.contains("/api/social/friends/requests") && it.url.contains("?page=2")
            }
            .bind(MockHttpActionService.Response(200).body(ArrayList<FriendCandidate>())) {
               it.url.contains("/api/social/friends/requests") && it.url.contains("?page=3")
            }
            .build()
   }

   fun mockHttpServiceForError(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(MockHttpActionService.Response(422)) {
               it.url.contains("/api/social/friends/requests")
            }.build()
   }
})
