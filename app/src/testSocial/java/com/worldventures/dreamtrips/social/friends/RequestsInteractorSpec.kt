package com.worldventures.dreamtrips.social.friends

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.api.friends.model.ImmutableFriendCandidate
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheResultWrapper
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.modules.friends.service.command.GetRequestsCommand
import com.worldventures.dreamtrips.modules.friends.storage.RequestsStorage
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RequestsInteractorSpec : BaseSpec({

   describe("Receive request list successfully") {

      setup(mockHttpServiceForSuccessResult())

      context("First page is received and processed correctly") {
         val testSubscriber = TestSubscriber<ActionState<GetRequestsCommand>>()
         val firstPageRequest = GetRequestsCommand(1)
         friendsInteractor.requestsPipe
               .createObservable(firstPageRequest)
               .subscribe(testSubscriber)

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

      context("Pagination should proceed for second page as well") {
         val testSubscriber = TestSubscriber<ActionState<GetRequestsCommand>>()
         val secondPageRequest = GetRequestsCommand(2)
         friendsInteractor.requestsPipe
               .createObservable(secondPageRequest)
               .subscribe(testSubscriber)

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

      context("Pagination should stop, because there is no more elements") {
         val testSubscriber = TestSubscriber<ActionState<GetRequestsCommand>>()
         val thirdPageRequest = GetRequestsCommand(3)
         friendsInteractor.requestsPipe
               .createObservable(thirdPageRequest)
               .subscribe(testSubscriber)

         it("should indicate that it isn't first page and errors didn't occur") {
            AssertUtil.assertActionSuccess(testSubscriber, { !thirdPageRequest.isFirstPage })
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

   describe("Error during receiving request list") {

      setup(mockHttpServiceForError())

      context("Nothing should happen except thrown exception") {
         val testSubscriber = TestSubscriber<ActionState<GetRequestsCommand>>()
         val firstPageRequest = GetRequestsCommand(1)
         friendsInteractor.requestsPipe
               .createObservable(firstPageRequest)
               .subscribe(testSubscriber)

         it("exception should be thrown and result should be empty") {
            AssertUtil.assertActionFail(testSubscriber, { firstPageRequest.items().isEmpty() })
         }
      }

   }

}) {
   companion object {

      val PAGE_SIZE = 100
      lateinit var friendsInteractor: FriendsInteractor
      val mapperyContext: MapperyContext = mock()

      val apiUsersPage1: List<FriendCandidate> = (1..PAGE_SIZE).map { it -> mockUserCandidate(it) }.toList()
      val apiUsersPage2: List<FriendCandidate> = listOf(mockUserCandidate(PAGE_SIZE + 1))

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()
         val storage = RequestsStorage()
         val commandCacheWrapper = CacheResultWrapper(daggerCommandActionService)
         commandCacheWrapper.bindStorage(storage.actionClass, storage)

         val janet = Janet.Builder()
               .addService(commandCacheWrapper)
               .addService(httpService)
               .build()

         whenever(mapperyContext.convert(com.nhaarman.mockito_kotlin.any(), eq(User::class.java))).thenAnswer {
            if (it.arguments[0] is List<*>) {
               val source = it.arguments[0] as List<FriendCandidate>
               val users = source.map {
                  var user: User = mock()
                  user.id = it.id()
                  user
               }.toList()
               users
            } else ArrayList<FriendCandidate>()
         }

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mapperyContext }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      fun mockUserCandidate(number: Int): FriendCandidate {
         val avatar = ImmutableAvatar.builder()
               .medium("test")
               .original("test")
               .thumb("test")
               .build()

         return ImmutableFriendCandidate.builder()
               .id(number)
               .username(number.toString())
               .firstName("first_name")
               .lastName("last_name")
               .avatar(avatar)
               .location("test")
               .badges(ArrayList())
               .build()
      }

      fun mockHttpServiceForSuccessResult(): MockHttpActionService {
         return MockHttpActionService.Builder()
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
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422)) { it.url.contains("/api/social/friends/requests") }
               .build()
      }
   }
}