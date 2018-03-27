package com.worldventures.dreamtrips.social.service.users.friend.command

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
import io.techery.janet.http.test.MockHttpActionService.Response
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import com.nhaarman.mockito_kotlin.any as anyMockito

class GetFriendsCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<GetFriendsCommand>() {

   private val PAGE_SIZE = 100
   private val PATH = "api/social/friends"
   private val QUERY_PAGE_PARAM = "?page="
   private val apiFriendsPage1 = (1..PAGE_SIZE).map { mockFriendCandidate(it) }.toList()
   private val apiFriendsPage2 = listOf(mockFriendCandidate(PAGE_SIZE + 1))

   override fun create(): Spec.() -> Unit = {
      describe("Should successfully send query and receive values") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         describe("Send search query for first page should return result") {
            it("Should contains 100 received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(GetFriendsCommand("", 1, PAGE_SIZE))) { it.result.size == apiFriendsPage1.size }
            }

            it("Should inform that more pages left") {
               AssertUtil.assertActionSuccess(sendCommand(GetFriendsCommand("", 1, PAGE_SIZE))) { it.result.size > 0 }
            }
         }

         describe("Pagination with the same search query should proceed for nex page") {
            it("Should 1 from current page received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(GetFriendsCommand("", 2, PAGE_SIZE))) { it.result.size == apiFriendsPage2.size }
            }

            it("Should inform that pagination can be proceed") {
               AssertUtil.assertActionSuccess(sendCommand(GetFriendsCommand("", 2, PAGE_SIZE))) { it.result.isNotEmpty() }
            }
         }

         describe("Pagination should stop, because prev page was the last and no received entities") {
            it("Shouldn't have entities and pagination can't proceed") {
               AssertUtil.assertActionSuccess(sendCommand(GetFriendsCommand("", 3, PAGE_SIZE))) { it.result.isEmpty() }
            }
         }
      }

      describe("Error during receiving friend entities") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(GetFriendsCommand("", 1, PAGE_SIZE))) { it != null }
         }
      }
   }

   override fun sendCommand(command: GetFriendsCommand) = TestSubscriber<ActionState<GetFriendsCommand>>().apply {
      interactor.friendsPipe.createObservable(command).subscribe(this)
   }

   override fun mockCommandCacheWrapper(daggerActionService: MockDaggerActionService) = CacheResultWrapper(daggerActionService)

   override fun mockDaggerActionService() = CommandActionService().wrapDagger().apply {
      val mapperyContext = mock<MapperyContext>()
      whenever(mapperyContext.convert(anyMockito(), eq(User::class.java))).thenAnswer {
         if (it.arguments[0] is List<*>)
            (it.arguments[0] as List<*>).map { any ->
               mock<User>().apply { id = (any as FriendCandidate).id() }
            }.toList()
         else ArrayList<FriendCandidate>()
      }
      registerProvider(MapperyContext::class.java, { mapperyContext })
   }

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(Response(200).body(apiFriendsPage1)) { checkUrl(it.url, 1) }
            .bind(Response(200).body(apiFriendsPage2)) { checkUrl(it.url, 2) }
            .bind(Response(200).body(ArrayList<FriendCandidate>())) { checkUrl(it.url, 3) }
            .build()
   }

   private fun mockHttpServiceForError(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(MockHttpActionService.Response(422)) {
               it.url.contains("api/") && it.url.contains("likes")
            }.build()
   }

   private fun checkUrl(url: String, page: Int): Boolean {
      return url.contains(PATH, true)
            && url.contains(QUERY_PAGE_PARAM + page, true)
   }
})

