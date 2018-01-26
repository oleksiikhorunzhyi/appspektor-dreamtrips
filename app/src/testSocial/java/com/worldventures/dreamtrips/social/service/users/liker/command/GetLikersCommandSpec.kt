package com.worldventures.dreamtrips.social.service.users.liker.command

import android.text.TextUtils
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.cache.CacheResultWrapper
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.core.test.AssertUtil
import com.worldventures.core.test.janet.MockDaggerActionService
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.api.friends.model.FriendCandidate
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.service.users.base.command.BaseHttpUsersCommandTestBody
import com.worldventures.dreamtrips.social.ui.feed.model.BaseFeedEntity
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class GetLikersCommandSpec : BaseBodySpec(object : BaseHttpUsersCommandTestBody<GetLikersCommand>() {

   private val PAGE_SIZE = 100
   private val apiLikersFriendsPage1 = (1..PAGE_SIZE).map { mockFriendCandidate(it) }.toList()
   private val apiLikersFriendsPage2 = listOf(mockFriendCandidate(PAGE_SIZE + 1))

   override fun create(): Spec.() -> Unit = {
      describe("Should successfully send query and receive values") {
         beforeEachTest { setup(mockHttpServiceForSuccessResult()) }

         describe("Should contains 100 received and converted searched entities from first page " +
               "and first liker should equals first user in users") {
            it("Should contains 100 received entities") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size == apiLikersFriendsPage1.size }
            }

            it("Feed entity should contain first liker name and it should equals " +
                  "first user in users") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.feedEntity.firstLikerName == it.result[0].fullName }
            }

            it("Should inform that more pages left") {
               AssertUtil.assertActionSuccess(sendCommand(1)) { it.result.size > 0 }
            }
         }

         describe("Should contains 1 received entity from second page") {
            it("Should contains 1 received entities") {
               AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size == apiLikersFriendsPage2.size }
            }

            it("Should inform that pagination can be proceed") {
               AssertUtil.assertActionSuccess(sendCommand(2)) { it.result.size > 0 }
            }
         }

         describe("Pagination should stop" +
               ", because prev page was the last and no received entities") {
            it("Result should be empty") {
               AssertUtil.assertActionSuccess(sendCommand(3)) { it.result.isEmpty() }
            }
         }
      }

      describe("Error during receiving likers entities") {
         beforeEachTest { setup(mockHttpServiceForError()) }

         it("Exception should be thrown") {
            AssertUtil.assertActionFail(sendCommand(1)) { it != null }
         }
      }
   }

   private fun sendCommand(page: Int) = sendCommand(GetLikersCommand(mockFeedEntity(), page, PAGE_SIZE))

   override fun sendCommand(command: GetLikersCommand) = TestSubscriber<ActionState<GetLikersCommand>>().apply {
      interactor.likersPipe.createObservable(command).subscribe(this)
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
      registerProvider(SessionHolder::class.java, { mockSessionHolder() })
   }

   fun mockSessionHolder(): SessionHolder {
      val sessionHolder: SessionHolder = mock()
      val userSession: UserSession = mock()
      val user: User = mock()
      whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
      whenever(userSession.user()).thenReturn(user)
      return sessionHolder
   }

   private fun mockFeedEntity(): BaseFeedEntity {
      val baseFeedEntity: BaseFeedEntity = mock()
      baseFeedEntity.uid = "TrANJQTRz4"
      return baseFeedEntity
   }

   private fun mockHttpServiceForSuccessResult(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(MockHttpActionService.Response(200).body(apiLikersFriendsPage1)) { checkUrl(it.url, 1) }
            .bind(MockHttpActionService.Response(200).body(apiLikersFriendsPage2)) { checkUrl(it.url, 2) }
            .bind(MockHttpActionService.Response(200).body(ArrayList<FriendCandidate>())) { checkUrl(it.url, 3) }
            .build()
   }

   private fun mockHttpServiceForError(): MockHttpActionService {
      return mockHttpActionServiceBuidler()
            .bind(MockHttpActionService.Response(422)) {
               it.url.contains("api/") && it.url.contains("likes")
            }.build()
   }

   private fun checkUrl(url: String, page: Int): Boolean {
      return url.contains("api/")
            && !TextUtils.isEmpty(url.substring(url.indexOf("api/")
            + "api/".length, url.indexOf("/likes")))
            && url.contains("likes?page=$page")
   }

})

