package com.worldventures.dreamtrips.modules.social.bucket

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.core.test.StubServiceWrapper
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand
import com.worldventures.dreamtrips.modules.bucketlist.service.storage.BucketListDiskStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import org.mockito.Mockito
import rx.functions.Func1
import rx.observers.TestSubscriber
import java.util.*

class BucketListInteractorSpec : BucketInteractorBaseSpec({
    describe("bucket list actions") {
        setup()

        it("should contains all items from memory storage") {
            whenever(mockMemoryStorage.get(null))
                    .thenReturn(testListOfBucketsFromMemory)
            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber,
                    Func1 { bucketListAction -> testListOfBucketsFromMemory.containsAll(bucketListAction.result) })
        }

        it("should contains all items from database storage") {
            whenever(mockDb.readBucketList(BucketInteractorBaseTest.MOCK_USER_ID))
                    .thenReturn(testListOfBucketsFromDisk)
            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber,
                    Func1 { bucketListAction -> testListOfBucketsFromDisk.containsAll(bucketListAction.result) })
        }

        it("should contains all items from network") {
            val testSubscriber = loadBucketList(false)

            assertBucketListByPredicate(testSubscriber,
                    Func1 { bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.result) })
        }

        it("should contains all items ONLY from network") {
            whenever(mockMemoryStorage.get(null))
                    .thenReturn(testListOfBucketsFromMemory)
            val testSubscriber = loadBucketList(true)

            assertBucketListByPredicate(testSubscriber,
                    Func1 { bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.result) })
        }

        it("items should changed theirs positions") {
            val POSITION_FROM = 0
            val POSITION_TO = 1

            whenever(mockMemoryStorage.get(null))
                    .thenReturn(testListOfBucketsFromMemory)

            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            bucketInteractor.bucketListActionPipe()
                    .createObservable(BucketListCommand.move(POSITION_FROM, POSITION_TO, BucketItem.BucketType.LOCATION))
                    .subscribe(testSubscriber)

            assertActionSuccess(testSubscriber) { bucketListAction ->
                val items = bucketListAction.result
                testBucketItem1 == items[POSITION_TO] && testBucketItem2 == items[POSITION_FROM]
            }
        }
    }
}) {
    companion object {
        val testBucketItem1: BucketItem = mock()
        val testBucketItem2: BucketItem = mock()

        val testListOfBucketsFromMemory: List<BucketItem> = listOf(testBucketItem1, testBucketItem2)
        val testListOfBucketsFromDisk: List<BucketItem> = listOf(testBucketItem1)
        val testListOfBucketsFromNetwork: List<BucketItem> = listOf(testBucketItem1, testBucketItem2, mock())

        init {
            httpStubWrapper = StubServiceWrapper(mockHttpService())
            daggerCommandActionService = CommandActionService()
                    .wrapCache()
                    .bindStorageSet(storageSet())
                    .wrapDagger()

            whenever(testBucketItem1.uid).thenReturn("1")
            whenever(testBucketItem2.uid).thenReturn("2")

            whenever(testBucketItem1.type).thenReturn(BucketItem.BucketType.LOCATION.name)
            whenever(testBucketItem2.type).thenReturn(BucketItem.BucketType.LOCATION.name)
        }

        fun loadBucketList(force: Boolean): TestSubscriber<ActionState<BucketListCommand>> {
            val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
            val spyHttpCallback = Mockito.spy(StubServiceWrapper.Callback::class.java)
            httpStubWrapper.callback = spyHttpCallback

            bucketInteractor.bucketListActionPipe()
                    .createObservable(BucketListCommand.fetch(force))
                    .subscribe(testSubscriber)
            return testSubscriber
        }

        fun assertBucketListByPredicate(testSubscriber: TestSubscriber<ActionState<BucketListCommand>>,
                                        predicate: Func1<BucketListCommand, Boolean>): Unit = assertActionSuccess(testSubscriber, predicate)

        fun mockHttpService(): MockHttpActionService {
            return MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(testListOfBucketsFromNetwork))
                    { request -> request.url.contains("/bucket_list_items") && HttpAction.Method.GET.name == request.method }
                    .bind(MockHttpActionService.Response(200).body(JsonObject()))
                    { request -> request.url.contains("/position") }.build()
        }

        fun storageSet(): Set<ActionStorage<*>> {
            val storageSet = HashSet<ActionStorage<*>>()
            storageSet += BucketListDiskStorage(mockMemoryStorage, mockDb, mockSessionHolder)

            return storageSet
        }
    }
}