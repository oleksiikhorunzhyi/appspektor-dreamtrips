package com.worldventures.dreamtrips.social.feed.spek.notification

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity
import com.worldventures.dreamtrips.modules.feed.model.FeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationMemoryStorage
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.assertTrue

class NotificationFeedInteractorTest : BaseSpec({
   describe("Notification feed actions", {
      setup({ setOf(NotificationsStorage(mockDb, mockMemoryStorage)) }) { mockHttpService() }

      context("Refresh notifications") {
         on("Notifications cache is empty") {
            val testSubrciber = TestSubscriber<ActionState<GetNotificationsCommand>>()

            feedInteractor
                  .notificationsPipe()
                  .createObservable(GetNotificationsCommand.refresh())
                  .subscribe(testSubrciber)

            it("Should not call onProgress") {
               assertTrue { testSubrciber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } == null }
            }

            it("Items should contain new items") {
               AssertUtil.assertActionSuccess(testSubrciber) {
                  it.items.containsAll(notificationsList1)
               }
            }
         }
         on("Notifications cache is not empty") {
            doReturn(notificationsList2).whenever(mockMemoryStorage).get(any())

            val testSubrciber = TestSubscriber<ActionState<GetNotificationsCommand>>()

            feedInteractor
                  .notificationsPipe()
                  .createObservable(GetNotificationsCommand.refresh())
                  .subscribe(testSubrciber)

            it("Should call onProgress") {
               assertTrue { testSubrciber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
            }

            it("Items should contain only new items, and not contain cached ones") {
               AssertUtil.assertActionSuccess(testSubrciber) {
                  it.items.containsAll(notificationsList1) &&
                        !it.items.containsAll(notificationsList2)
               }
            }
         }
      }

      context("Load more notifications") {
         on("Notifications cache is not empty") {
            doReturn(notificationsList2).whenever(mockMemoryStorage).get(any())

            val testSubrciber = TestSubscriber<ActionState<GetNotificationsCommand>>()

            feedInteractor
                  .notificationsPipe()
                  .createObservable(GetNotificationsCommand.loadMore())
                  .subscribe(testSubrciber)

            it("Should call onProgress") {
               assertTrue { testSubrciber.onNextEvents.firstOrNull { it.status == ActionState.Status.PROGRESS } != null }
            }

            it("Should have cached items in onProgress") {
               assertTrue {
                  testSubrciber.onNextEvents.first {
                     it.status == ActionState.Status.PROGRESS
                  }.action.items.containsAll(notificationsList2)
               }
            }

            it("Items should contain new items + cached ones") {
               AssertUtil.assertActionSuccess(testSubrciber) {
                  it.items.containsAll(notificationsList1) &&
                        it.items.containsAll(notificationsList2)
               }
            }
         }
      }
   })
}) {
   companion object FeedCompanion {
      val mockDb: SnappyRepository = spy()
      val mockMemoryStorage: NotificationMemoryStorage = spy()

      lateinit var feedInteractor: NotificationFeedInteractor

      val notificationsParentList1 = ArrayList(listOf(generateFeedItem("1")))
      val notificationsList1 = notificationsParentList1.map { it.items[0] }
      val notificationsParentList2 = ArrayList(listOf(generateFeedItem("2")))
      val notificationsList2 = notificationsParentList2.map { it.items[0] }

      fun setup(storageSet: () -> Set<ActionStorage<*>>, httpService: () -> MockHttpActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(storageSet())
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(NotificationFeedInteractor::class.java) { feedInteractor }

         feedInteractor = NotificationFeedInteractor(janet)
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(notificationsParentList1)) { request ->
                  request.url.contains("/api/social/notifications")
               }.build()
      }

      fun generateFeedItem(uid: String): ParentFeedItem {
         val parentFeedItem = ParentFeedItem()
         val feedItem = FeedItem<FeedEntity>()
         val entity = TextualPost()
         entity.uid = uid
         feedItem.item = entity
         feedItem.createdAt = Date()

         parentFeedItem.type = "Single"
         parentFeedItem.items = listOf(feedItem)

         return parentFeedItem
      }

   }
}
