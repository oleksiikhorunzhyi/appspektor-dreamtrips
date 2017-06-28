package com.worldventures.dreamtrips.social.bucket

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.api.bucketlist.model.BucketItemSimple
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketMemoryStorage
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BucketListInteractorSpec : BucketInteractorBaseSpec({
   describe("bucket list actions") {

      context("memory storage is not empty") {
         it("should fetch bucket list from memory") {
            setup({ setOf(BucketListDiskStorage(mockMemoryStorage, mockDb)) }, mockDb, { mockHttpService() })

            doReturn(testListOfBucketsFromMemory)
                  .whenever(mockMemoryStorage).get(any())
            whenever(mockDb.readBucketList(any()))
                  .thenReturn(emptyList())

            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber) {
               testListOfBucketsFromMemory.containsAll(it.result)
            }
         }

         it("should changed items positions in memory") {
            val POSITION_FROM = 0
            val POSITION_TO = 1

            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            bucketInteractor.bucketListActionPipe()
                  .createObservable(BucketListCommand.move(POSITION_FROM, POSITION_TO, BucketItem.BucketType.LOCATION))
                  .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber) {
               val items = it.result
               testBucketItem1 == items[POSITION_TO] && testBucketItem2 == items[POSITION_FROM]
            }
         }
      }

      context("memory storage is empty and database storage is not") {
         it("should fetch from database") {
            doReturn(emptyList<BucketItem>())
                  .whenever(mockMemoryStorage).get(any())
            whenever(mockDb.readBucketList(any()))
                  .thenReturn(testListOfBucketsFromDisk)

            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber) {
               testListOfBucketsFromDisk.containsAll(it.result)
            }
         }
      }

      context("memory storage and database storage are empty") {
         it("should fetch from network") {
            doReturn(emptyList<BucketItem>())
                  .whenever(mockMemoryStorage).get(any())
            whenever(mockDb.readBucketList(any()))
                  .thenReturn(emptyList())

            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber) {
               validateResponse(it.result, testListOfBucketsFromNetwork)
            }
         }
      }

      context("memory storage and database storage are not empty") {
         it("force load should fetch only from network") {
            val testSubscriber = loadBucketList(true)

            assertBucketListByPredicate(testSubscriber) {
               validateResponse(it.result, testListOfBucketsFromNetwork)
            }
         }
      }

      context("verify save to all storage types") {
         it("should save list of buckets into memory 5 times") {
            verify(mockMemoryStorage, VerificationModeFactory.times(5)).save(any(), any())
         }

         it("should save list of buckets into database 5 times") {
            verify(mockDb, VerificationModeFactory.times(5)).saveBucketList(any(), any())
         }
      }
   }
}) {
   companion object {
      val testBucketItem1: BucketItem = mock()
      val testBucketItem2: BucketItem = mock()
      val testNetworkBucketItem1: BucketItemSimple = getStubApiBucket(1)
      val testNetworkBucketItem2: BucketItemSimple = getStubApiBucket(2)

      val testListOfBucketsFromMemory: List<BucketItem> = mutableListOf(testBucketItem1, testBucketItem2)
      val testListOfBucketsFromDisk: List<BucketItem> = mutableListOf(testBucketItem1)
      val testListOfBucketsFromNetwork: List<BucketItemSimple> = mutableListOf(testNetworkBucketItem1, testNetworkBucketItem2)

      var mockMemoryStorage: BucketMemoryStorage = spy()
      val mockDb: SnappyRepository = spy()

      init {

         whenever(testBucketItem1.uid).thenReturn("1")
         whenever(testBucketItem2.uid).thenReturn("2")

         whenever(testBucketItem1.type).thenReturn(BucketItem.BucketType.LOCATION.name)
         whenever(testBucketItem2.type).thenReturn(BucketItem.BucketType.LOCATION.name)
      }

      fun loadBucketList(force: Boolean): TestSubscriber<ActionState<BucketListCommand>> {
         val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()

         bucketInteractor.bucketListActionPipe()
               .createObservable(BucketListCommand.fetch(force))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun assertBucketListByPredicate(testSubscriber: TestSubscriber<ActionState<BucketListCommand>>,
                                      predicate: (command: BucketListCommand) -> Boolean): Unit =
            assertActionSuccess(testSubscriber, { predicate(it) })

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200).body(testListOfBucketsFromNetwork))
               { request -> request.url.contains("/bucket_list_items") && HttpAction.Method.GET.name == request.method }
               .bind(MockHttpActionService.Response(200).body(JsonObject()))
               { request -> request.url.contains("/position") }.build()
      }

      fun validateResponse(localBucketItems: List<BucketItem>,
                           apiBucketItems: List<com.worldventures.dreamtrips.api.bucketlist.model.BucketItem>): Boolean {
         assertTrue(localBucketItems.size == apiBucketItems.size)
         for (i in 0..localBucketItems.size - 1) {
            val bucketItem = localBucketItems[i]
            val apiBucketItem = apiBucketItems[i]
            assertEquals(bucketItem.uid, apiBucketItem.uid())
            assertEquals(bucketItem.name, apiBucketItem.name())
         }
         return true
      }
   }
}
