package com.worldventures.dreamtrips.social.bucket.spek

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import org.mockito.internal.verification.VerificationModeFactory
import rx.observers.TestSubscriber

class BucketListInteractorSpec : BucketInteractorBaseSpec({
   describe("bucket list actions") {
      setup({ setOf(BucketListDiskStorage(mockMemoryStorage, mockDb)) }) { mockHttpService() }

      context("memory storage is not empty") {
         it("should fetch bucket list from memory") {
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
               testListOfBucketsFromNetwork.containsAll(it.result)
            }
         }
      }

      context("memory storage and database storage are not empty") {
         it("force load should fetch only from network") {
            val testSubscriber = loadBucketList(true)

            assertBucketListByPredicate(testSubscriber) {
               testListOfBucketsFromNetwork.containsAll(it.result)
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

      val testListOfBucketsFromMemory: List<BucketItem> = mutableListOf(testBucketItem1, testBucketItem2)
      val testListOfBucketsFromDisk: List<BucketItem> = mutableListOf(testBucketItem1)
      val testListOfBucketsFromNetwork: List<BucketItem> = mutableListOf(testBucketItem1, testBucketItem2, mock())

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
   }
}
