package com.worldventures.dreamtrips.social.friends.spec.interactor

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.command.GetFriendsCommand
import com.worldventures.dreamtrips.social.ui.friends.storage.RequestsStorage
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import kotlin.test.assertTrue

class GetFriendsCommandSpec : BaseSpec({
   describe("Should successfully send query and receive values") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Send search query for first page should return result") {
         it("Should contains 100 received and converted searched entities") {
            AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size == apiFriendsPage1.size }
         }

         it("Should inform that more pages left") {
            AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size > 0 }
         }
      }

      context("Pagination with the same search query should proceed for nex page") {
         it("Should 1 from current page received and converted searched entities") {
            AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size == apiFriendsPage2.size }
         }

         it("Should inform that pagination can be proceed") {
            AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size > 0 }
         }
      }

      context("Pagination should stop, because prev page was the last and no received entities") {
         it("Shouldn't have entities and pagination can't proceed") {
            AssertUtil.assertActionSuccess(sendCommand(3)) { it.result.isEmpty() }
         }
      }
   }

   describe("Error during receiving friend entities") {
      beforeEachTest { setup(mockHttpServiceForError()) }

      context("Here must throw Exception") {
         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(1)) { it != null }
         }
      }
   }
}) {
   companion object {
      private const val PAGE_SIZE = 100
      private lateinit var friendsInteractor: FriendsInteractor
      private val apiFriendsPage1 = (1..PAGE_SIZE).map { MockUtil.mockFriendCandidate(it) }.toList()
      private val apiFriendsPage2 = listOf(MockUtil.mockFriendCandidate(PAGE_SIZE + 1))
      private const val PATH = "api/social/friends"
      private const val QUERY_PAGE_PARAM = "?page="

      private fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()
         val storage = RequestsStorage()
         val commandCacheWrapper = CacheResultWrapper(daggerCommandActionService)
         commandCacheWrapper.bindStorage(storage.actionClass, storage)

         val janet = Janet.Builder()
               .addService(commandCacheWrapper)
               .addService(httpService)
               .build()

         val mapperyContext: MapperyContext = mock()

         whenever(mapperyContext.convert(com.nhaarman.mockito_kotlin.any(), eq(User::class.java)))
               .thenAnswer {
                  if (it.arguments[0] is List<*>) {
                     val source = it.arguments[0] as List<*>
                     val users = source.map {
                        val user: User = mock()
                        user.id = (it as FriendCandidate).id()
                        user
                     }.toList()
                     users
                  } else ArrayList<FriendCandidate>()
               }

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mapperyContext }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      private fun mockHttpServiceForSuccessResult(): MockHttpActionService = MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(200).body(apiFriendsPage1), {
               checkUrl(it.url, 1)
            })
            .bind(MockHttpActionService.Response(200).body(apiFriendsPage2), {
               checkUrl(it.url, 2)
            })
            .bind(MockHttpActionService.Response(200).body(ArrayList<FriendCandidate>()), {
               checkUrl(it.url, 3)
            })
            .build()

      private fun mockHttpServiceForError(): MockHttpActionService = MockHttpActionService.Builder()
            .bind(MockHttpActionService.Response(422)) {
               it.url.contains("api/") && it.url.contains("likes")
            }
            .build()

      private fun checkUrl(url: String, page: Int): Boolean {
         return url.contains(PATH, true)
               && url.contains(QUERY_PAGE_PARAM + page, true)
      }

      private fun sendCommand(page: Int): TestSubscriber<ActionState<GetFriendsCommand>> {
         return TestSubscriber<ActionState<GetFriendsCommand>>().apply {
            friendsInteractor.friendsPipe
                  .createObservable(GetFriendsCommand("", page, PAGE_SIZE))
                  .subscribe(this)
         }
      }
   }
}