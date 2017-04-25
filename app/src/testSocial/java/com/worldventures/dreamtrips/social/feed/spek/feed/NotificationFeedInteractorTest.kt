package com.worldventures.dreamtrips.social.feed.spek.notification

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.entity.model.BaseEntityHolder
import com.worldventures.dreamtrips.api.feed.converter.ImmutableObjFeedItem
import com.worldventures.dreamtrips.api.feed.model.FeedItemWrapper
import com.worldventures.dreamtrips.api.feed.model.ImmutableFeedItemLinks
import com.worldventures.dreamtrips.api.feed.model.ImmutableFeedItemWrapper
import com.worldventures.dreamtrips.api.messenger.model.response.ImmutableShortUserProfile
import com.worldventures.dreamtrips.api.post.model.response.ImmutableLocation
import com.worldventures.dreamtrips.api.post.model.response.ImmutablePostSocialized
import com.worldventures.dreamtrips.api.post.model.response.PostSocialized
import com.worldventures.dreamtrips.api.session.model.ImmutableAvatar
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.feed.converter.FeedItemConverter
import com.worldventures.dreamtrips.modules.feed.converter.LinksConverter
import com.worldventures.dreamtrips.modules.feed.converter.PostSocializedConverter
import com.worldventures.dreamtrips.modules.feed.model.FeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.service.NotificationFeedInteractor
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationMemoryStorage
import com.worldventures.dreamtrips.modules.feed.service.storage.NotificationsStorage
import com.worldventures.dreamtrips.modules.mapping.converter.Converter
import com.worldventures.dreamtrips.modules.mapping.converter.LocationConverter
import com.worldventures.dreamtrips.modules.mapping.converter.ShortProfilesConverter
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.*
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.assertTrue

class NotificationFeedInteractorTest : BaseSpec({
   describe("Notification feed actions", {
      setup({ setOf(NotificationsStorage(mockDb, mockMemoryStorage)) }) { mockHttpService() }

      context("Refresh notifications") {
         context("Notifications cache is empty") {
            val testSubrciber = TestSubscriber<ActionState<GetNotificationsCommand>>()

            feedInteractor
                  .notificationsPipe()
                  .createObservable(GetNotificationsCommand.refresh())
                  .subscribe(testSubrciber)

            xit("Should not call onProgress") {
               assertTrue {
                  testSubrciber.onNextEvents.firstOrNull {
                     it.status == ActionState.Status.PROGRESS } == null }
            }

            it("Items should contain new items") {
               AssertUtil.assertActionSuccess(testSubrciber) {
                  it.items.containsAll(notificationsList1)
               }
            }
         }
         context("Notifications cache is not empty") {
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
         context("Notifications cache is not empty") {
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

      lateinit var notificationsParentList1: List<FeedItemWrapper>
      lateinit var notificationsList1: List<FeedItem<TextualPost>>
      lateinit var notificationsParentList2: List<FeedItemWrapper>
      lateinit var notificationsList2: List<FeedItem<TextualPost>>

      fun setup(storageSet: () -> Set<ActionStorage<*>>, httpService: () -> MockHttpActionService) {
         val mappery = getMappery()

         notificationsParentList1 = listOf(generateParentItem("1"))
         notificationsList1 = mappery.convert(notificationsParentList1.get(0).items(), FeedItem::class.java) as List<FeedItem<TextualPost>>

         notificationsParentList2 = listOf(generateParentItem("2"))
         notificationsList2 = mappery.convert(notificationsParentList2.get(0).items(), FeedItem::class.java) as List<FeedItem<TextualPost>>

         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(storageSet())
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()


         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(NotificationFeedInteractor::class.java) { feedInteractor }

         feedInteractor = NotificationFeedInteractor(SessionActionPipeCreator(janet))
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(notificationsParentList1)) { request ->
                  request.url.contains("/api/social/notifications")
               }.build()
      }

      fun generateParentItem(uid: String): FeedItemWrapper {
         val parentFeedItem = ImmutableFeedItemWrapper.builder()
         parentFeedItem.type(FeedItemWrapper.Type.SINGLE)

         val feedItem = ImmutableObjFeedItem.builder<PostSocialized>()

         val entity = ImmutablePostSocialized.builder()
         var profile = ImmutableShortUserProfile.builder()
            .id(1)
            .username("Test")
            .firstName("Test")
            .lastName("Test")
            .avatar(ImmutableAvatar.builder().original("").medium("").thumb("").build())
            .badges(listOf(""))
            .build()
         entity.owner(profile)
         entity.uid(uid)
         entity.description(uid)
         entity.createdAt(Date())
         entity.updatedAt(Date())
         entity.liked(false)
         entity.likes(1)
         entity.location(ImmutableLocation.builder().lat(1.0).lng(1.0).name("1").build())
         entity.commentsCount(1)

         feedItem.entity(entity.build())
         feedItem.createdAt(Date())
         feedItem.action(com.worldventures.dreamtrips.api.feed.model.FeedItem.Action.COMMENT)
         feedItem.links(ImmutableFeedItemLinks.builder().addAllUsers(listOf(profile)).build())
         feedItem.type(BaseEntityHolder.Type.BUCKET_LIST_ITEM)

         parentFeedItem.addAllItems(listOf(feedItem.build()))

         return parentFeedItem.build()
      }

      private fun getMappery(): Mappery {
         val builder = Mappery.Builder()
         for (converter in constructConverters()) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter)
         }
         return builder.build()
      }

      fun constructConverters(): List<Converter<Any, Any>> {
         return listOf(castConverter(FeedItemConverter()),
               castConverter(PostSocializedConverter()),
               castConverter(ShortProfilesConverter()),
               castConverter(LocationConverter()),
               castConverter(LinksConverter()))
      }

      // TODO Improve this, there should not be need for cast, but currently
      // it's not compilable if we return Converter<*, *>
      fun castConverter(converter: Converter<*, *>): Converter<Any, Any> {
         return converter as Converter<Any, Any>
      }
   }
}
