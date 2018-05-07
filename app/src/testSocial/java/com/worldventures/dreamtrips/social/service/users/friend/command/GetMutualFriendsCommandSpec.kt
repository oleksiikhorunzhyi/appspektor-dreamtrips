package com.worldventures.dreamtrips.social.service.users.friend.command

import android.text.TextUtils
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
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class GetMutualFriendsCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<GetMutualFriendsCommand>() {

   private val PAGE_SIZE = 100
   private val BASE_PATH = "api/social/friends/"
   private val SECONDARY_PATH = "/mutual"
   private val QUERY_PAGE_PARAM = "?page="
   private val apiMutualFriendsPage1 = (1..PAGE_SIZE).map { mockFriendCandidate(it) }.toList()
   private val apiMutualFriendsPage2 = listOf(mockFriendCandidate(PAGE_SIZE + 1))

   override fun create(): Spec.() -> Unit = {
      describe("Should successfully send query and receive values") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         describe("Send search query for first page should return result") {
            it("Should contains 100 received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size == apiMutualFriendsPage1.size }
            }

            it("Should inform that more pages left") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size > 0 }
            }
         }

         describe("Pagination with the same search query should proceed for nex page") {
            it("Should 1 from current page received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size == apiMutualFriendsPage2.size }
            }

            it("Should inform that pagination can be proceed") {
               AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size > 0 }
            }
         }

         describe("Pagination should stop, because prev page was the last and no received entities") {
            it("Shouldn't have entities and pagination can't proceed") {
               AssertUtil.assertActionSuccess(sendCommand(3)) { it.result.isEmpty() }
            }
         }
      }

      describe("Here must throw Exception") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(3)) { it != null }
         }
      }
   }

   private fun sendCommand(page: Int) = sendCommand(GetMutualFriendsCommand(100500, page, PAGE_SIZE))

   override fun sendCommand(command: GetMutualFriendsCommand) = TestSubscriber<ActionState<GetMutualFriendsCommand>>().apply {
      interactor.mutualFriendsPipe.createObservable(command).subscribe(this)
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

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

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200).body(apiMutualFriendsPage1)) {
               checkUrl(it.url, 1)
            }
            .bind(MockHttpActionService.Response(200).body(apiMutualFriendsPage2)) {
               checkUrl(it.url, 2)
            }
            .bind(MockHttpActionService.Response(200).body(ArrayList<FriendCandidate>())) {
               checkUrl(it.url, 3)
            }
            .build()
   }

   private fun mockHttpServiceForError(): MockHttpActionService {
      return MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(422)) {
               it.url.contains("/api/social/friends/requests")
            }.build()
   }

   private fun checkUrl(url: String, page: Int): Boolean {
      return url.contains(BASE_PATH, true)
            && url.contains(QUERY_PAGE_PARAM + page, true)
            && url.contains(SECONDARY_PATH)
            && !TextUtils.isEmpty(url.substring(url.indexOf(BASE_PATH) + BASE_PATH.length
            , url.indexOf(SECONDARY_PATH)))
   }
})

