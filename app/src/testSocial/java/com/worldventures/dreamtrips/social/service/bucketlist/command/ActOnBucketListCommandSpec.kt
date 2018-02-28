package com.worldventures.dreamtrips.social.service.bucketlist.command

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.model.User
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec.Companion.bindStorageSet
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapCache
import com.worldventures.dreamtrips.BaseSpec.Companion.wrapDagger
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple
import com.worldventures.dreamtrips.api.bucketlist.model.BucketStatus
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType
import com.worldventures.dreamtrips.api.bucketlist.model.ImmutableBucketItemSimple
import com.worldventures.dreamtrips.social.common.base.BaseBodySpec
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.social.ui.bucketlist.service.storage.BucketMemoryStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import java.util.Date
import java.util.Random

class ActOnBucketListCommandSpec : BaseBodySpec(object : BaseBucketListCommandTestBody() {

   private var user = mockUser(1)
   private var bucektApiItems = (1..2).map { mockApiBucketItem(it) }.toList()
   private var bucketLocalItems = (1..3).map { mockBucketItem("$it") }.toList()
   private var mockDb: SocialSnappyRepository = spy()
   private var memoryStorage: BucketMemoryStorage = mock()

   override fun create(): Spec.() -> Unit = {
      describe("Actions succeed") {
         beforeEachTest { setup(mockHttpServiceForSuccess()) }

         it("Should fetch item with force update") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.fetch(user, true))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.size == bucektApiItems.size && it.result.firstOrNull { it.owner != user } == null
            }
         }

         it("Should fetch item without force update") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems)

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.fetch(user, false))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.size == bucketLocalItems.size
            }
         }

         it("Should create item") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems.toList())

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.createItem(bucketItem))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.size == bucketLocalItems.size + 1
                     && it.result.firstOrNull { item -> item.uid == bucketItem.uid } != null
            }
         }

         it("Should update item") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems)

            val updatedBucketItem = mockBucketItem("1").apply {
               status = BucketItem.COMPLETED
            }

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.updateItem(updatedBucketItem))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.let { it[it.indexOf(updatedBucketItem)]?.status == BucketItem.COMPLETED }
            }
         }

         it("Should delete item") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            val removedItemId = "100500"
            val removedItem = mockBucketItem(removedItemId)

            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems.toMutableList()
                  .apply { add(removedItem) })

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.deleteItem(removedItemId))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               it.result.let {
                  it.size == bucketLocalItems.size && !it.contains(removedItem)
               }
            }
         }

         it("Should move item") {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            val randomType = BucketItem.BucketType.values().let { it[Random().nextInt(it.size - 1)] }

            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems.toMutableList().apply {
               forEach { it.type = randomType.getName() }
            })

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.move(0, 2, randomType))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionSuccess(testSubscriber) {
               bucketLocalItems[0] == it.result[2] && bucketLocalItems[2] == it.result[1]
            }
         }
      }

      describe("Error while executing command") {
         it("Should catch error and return cashed result") {
            setup(mockHttpServiceForError())
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            whenever(mockDb.readBucketList(any())).thenReturn(bucketLocalItems.toList())

            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.fetch(user, true))
                  .subscribe(testSubscriber)

            AssertUtil.assertActionFail(testSubscriber) { it != null }
         }
      }
   }

   private fun mockUser(id: Int) = User().apply {
      this.id = id
   }

   override fun mockDaggerActionService() = CommandActionService()
         .wrapCache().bindStorageSet(setOf(BucketListDiskStorage(memoryStorage, mockDb)))
         .wrapDagger().apply {
      registerProvider(SocialSnappyRepository::class.java) { mockDb }
   }

   override fun mockHttpServiceForSuccess() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(200).body(bucektApiItems)) {
            it.url.contains("/api/users/") && it.url.contains("/bucket_list_items")
         }
         .bind(MockHttpActionService.Response(200).body(Any())) {
            it.url.contains("/api/bucket_list_items/") && it.url.contains("/position")
         }
         .build()

   override fun mockHttpServiceForError() = MockHttpActionService.Builder()
         .bind(MockHttpActionService.Response(400).body(listOf<BucketItemSimple>())) {
            it.url.contains("/api/users/") && it.url.contains("/bucket_list_items")
         }.build()

   private fun mockApiBucketItem(id: Int): BucketItemSimple {
      return ImmutableBucketItemSimple.builder()
            .id(id)
            .uid("$id")
            .creationDate(Date())
            .link("")
            .name("$id")
            .type(BucketType.ACTIVITY)
            .status(BucketStatus.NEW)
            .bucketPhoto(emptyList())
            .tags(emptyList())
            .friends(emptyList())
            .build()
   }
})
