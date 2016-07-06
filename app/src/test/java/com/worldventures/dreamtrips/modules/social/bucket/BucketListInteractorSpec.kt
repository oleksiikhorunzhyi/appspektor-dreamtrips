package com.worldventures.dreamtrips.modules.social.bucket

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import rx.functions.Func1
import rx.observers.TestSubscriber
import kotlin.test.assertNotNull

class BucketListInteractorSpec : BucketInteractorBaseSpec({
    beforeEach {
        mockMemoryStorage = spy()
        mockDb = spy()

        httpStubWrapper = mockHttpService().wrapStub()
        httpStubWrapper.callback = spy()

        daggerCommandActionService = CommandActionService()
                .wrapCache()
                .bindStorageSet(storageSet())
                .wrapDagger()

        setup()
    }

    describe("bucket list actions") {
        context("memory storage is not empty") {
            beforeEach {
                whenever(mockMemoryStorage.get(null))
                        .thenReturn(testListOfBucketsFromMemory)
            }

            it("should fetch bucket list from memory") {
                val testSubscriber = loadBucketList(false)

                assertBucketListByPredicate(testSubscriber,
                        Func1 { bucketListAction -> testListOfBucketsFromMemory.containsAll(bucketListAction.result) })
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
            beforeEach {
                whenever(mockDb.readBucketList(MOCK_USER_ID))
                        .thenReturn(testListOfBucketsFromDisk)
            }

            it("should fetch from database") {
                val testSubscriber = loadBucketList(false)

                assertBucketListByPredicate(testSubscriber,
                        Func1 { bucketListAction -> testListOfBucketsFromDisk.containsAll(bucketListAction.result) })
            }

            it("should fill memory storage") {
                assertNotNull(mockMemoryStorage) {
                    it.get(null) != null
                }
            }
        }

        context("memory storage and database storage are empty") {
            it("should fetch from network") {
                val testSubscriber = loadBucketList(false)

                assertBucketListByPredicate(testSubscriber,
                        Func1 { bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.result) })
            }

            it("should fill memory storage") {
                assertNotNull(mockMemoryStorage) {
                    it.get(null) != null
                }
            }

            it("should fill database storage") {
                assertNotNull(mockDb) {
                    it.readBucketList(MOCK_USER_ID) != null
                }
            }
        }

        context("memory storage and database storage are not empty") {
            it("force load should fetch only from network") {
                val testSubscriber = loadBucketList(true)

                assertBucketListByPredicate(testSubscriber,
                        Func1 { bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.result) })
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
                                        predicate: Func1<BucketListCommand, Boolean>): Unit = assertActionSuccess(testSubscriber, predicate)

        fun mockHttpService(): MockHttpActionService {
            return MockHttpActionService.Builder()
                    .bind(MockHttpActionService.Response(200).body(testListOfBucketsFromNetwork))
                    { request -> request.url.contains("/bucket_list_items") && HttpAction.Method.GET.name == request.method }
                    .bind(MockHttpActionService.Response(200).body(JsonObject()))
                    { request -> request.url.contains("/position") }.build()
        }
    }
}