package com.worldventures.dreamtrips.social.service.users.search.command

import android.text.TextUtils
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.cache.CacheResultWrapper
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
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import com.nhaarman.mockito_kotlin.any as anyMockito

class GetSearchUsersCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<GetSearchUsersCommand>() {

   private val PAGE_SIZE = 100
   private val apiSearchUsersPage1 = (1..PAGE_SIZE).map { mockFriendCandidate(it) }.toList()
   private val apiSearchUsersPage2 = listOf(mockFriendCandidate(PAGE_SIZE + 1))

   override fun create(): SpecBody.() -> Unit = {
      describe("Searched users received successfully") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         describe("Send search query for first page should return result") {
            it("Should contains 100 received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size == apiSearchUsersPage1.size }
            }

            it("Should inform that more page left") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size > 0 }
            }
         }

         describe("Pagination with the same search query should proceed for nex page") {
            it("Should 1 from current page received and converted searched entities") {
               AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size == apiSearchUsersPage2.size }
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

      describe("Error during receiving searched entities") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(1)) { it != null }
         }
      }
   }

   private fun sendCommand(page: Int) = sendCommand(GetSearchUsersCommand("", page, PAGE_SIZE))

   override fun sendCommand(command: GetSearchUsersCommand) = TestSubscriber<ActionState<GetSearchUsersCommand>>().apply {
      interactor.searchUsersPipe.createObservable(command).subscribe(this)
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
            .bind(Response(200).body(apiSearchUsersPage1)) { checkUrl(it.url, 1) }
            .bind(Response(200).body(apiSearchUsersPage2)) { checkUrl(it.url, 2) }
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
      return url.contains("api/social/users") && url.contains("?page=" + page)
            && url.contains("query=")
            && !TextUtils.isEmpty(url.substring(url.indexOf("query=", 0, true)
            + "query=".length))
   }
})

