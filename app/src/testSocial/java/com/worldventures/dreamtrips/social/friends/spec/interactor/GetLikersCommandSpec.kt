package com.worldventures.dreamtrips.social.friends.spec.interactor

import android.text.TextUtils
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.social.friends.util.MockUtil
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand
import com.worldventures.dreamtrips.social.service.friends.storage.RequestsStorage
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


class GetLikersCommandSpec : BaseSpec({
   describe("Should successfully send query and receive values") {
      beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

      context("Should contains 100 received and converted searched entities from first page " +
            "and first liker should equals first user in list") {
         it("Should contains 100 received entities") {
            AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size == apiLikersFriendsPage1.size }
         }

         it("Feed entity should contain first liker name and it should equals " +
               "first user in list") {
            AssertUtil.assertActionSuccess(sendCommand(1)) { it.feedEntity.firstLikerName == it.result[0].fullName }
         }

         it("Should inform that more pages left") {
            AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size > 0 }
         }
      }

      context("Should contains 1 received entity from second page") {
         it("Should contains 1 received entities") {
            AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size == apiLikersFriendsPage2.size }
         }

         it("Should inform that pagination can be proceed") {
            AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size > 0 }
         }
      }

      context("Pagination should stop" +
            ", because prev page was the last and no received entities") {
         it("Result should be empty") {
            AssertUtil.assertActionSuccess(sendCommand(3)) { it.result.isEmpty() }
         }
      }
   }

   describe("Error during receiving likers entities") {
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
      private val apiLikersFriendsPage1 = (1..PAGE_SIZE).map { MockUtil.mockFriendCandidate(it) }.toList()
      private val apiLikersFriendsPage2 = listOf(MockUtil.mockFriendCandidate(PAGE_SIZE + 1))

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService().wrapDagger()
         val storage = RequestsStorage()
         val commandCacheWrapper = CacheResultWrapper(daggerCommandActionService)
         commandCacheWrapper.bindStorage(storage.actionClass, storage)

         val janet = Janet.Builder()
               .addService(commandCacheWrapper)
               .addService(httpService)
               .build()

         val mapperyContext: MapperyContext = mock()

         whenever(mapperyContext.convert(com.nhaarman.mockito_kotlin.any(), eq(User::class.java))).thenAnswer {
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
         daggerCommandActionService.registerProvider(SessionHolder::class.java) { MockUtil.mockSessionHolder() }

         friendsInteractor = FriendsInteractor(SessionActionPipeCreator(janet))
      }

      private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(apiLikersFriendsPage1), {
                  checkUrl(it.url, 1)
               })
               .bind(MockHttpActionService.Response(200).body(apiLikersFriendsPage2), {
                  checkUrl(it.url, 2)
               })
               .bind(MockHttpActionService.Response(200).body(ArrayList<FriendCandidate>()), {
                  checkUrl(it.url, 3)
               })
               .build()
      }

      private fun checkUrl(url: String, page: Int): Boolean {
         return url.contains("api/")
               && !TextUtils.isEmpty(url.substring(url.indexOf("api/") + "api/".length, url.indexOf("/likes")))
               && url.contains("likes?page=$page")
      }

      private fun mockFeedEntity(): BaseFeedEntity {
         val baseFeedEntity: BaseFeedEntity = mock()
         baseFeedEntity.uid = "TrANJQTRz4"
         return baseFeedEntity
      }

      private fun mockHttpServiceForError(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(422)) {
                  it.url.contains("api/")
                        && it.url.contains("likes")
               }
               .build()
      }

      private fun sendCommand(page: Int): TestSubscriber<ActionState<GetLikersCommand>> {
         return TestSubscriber<ActionState<GetLikersCommand>>().apply {
            friendsInteractor.likersPipe
                  .createObservable(GetLikersCommand(mockFeedEntity(), page, PAGE_SIZE))
                  .subscribe(this)
         }
      }
   }
}