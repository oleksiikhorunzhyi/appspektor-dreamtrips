package com.worldventures.dreamtrips.social.bucket.junit

import com.google.gson.JsonObject
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.janet.StubServiceWrapper
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand
import io.techery.janet.ActionState
import io.techery.janet.http.annotations.HttpAction
import io.techery.janet.http.test.MockHttpActionService
import org.assertj.core.util.Lists
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.modules.junit4.PowerMockRunner
import rx.functions.Func1
import rx.observers.TestSubscriber

@RunWith(PowerMockRunner::class)
class BucketListInteractorTest : BucketInteractorBaseTest() {
    private var testListOfBucketsFromMemory: List<BucketItem>? = null
    private var testListOfBucketsFromDisk: List<BucketItem>? = null
    private var testListOfBucketsFromNetwork: List<BucketItem>? = null

    private var testBucketItem1: BucketItem? = null
    private var testBucketItem2: BucketItem? = null

    init {
        testBucketItem1 = mock(BucketItem::class.java)
        testBucketItem2 = mock(BucketItem::class.java)
        `when`(testBucketItem1!!.uid).thenReturn("1")
        `when`(testBucketItem2!!.uid).thenReturn("2")

        `when`(testBucketItem1!!.type).thenReturn(BucketItem.BucketType.LOCATION.getName())
        `when`(testBucketItem2!!.type).thenReturn(BucketItem.BucketType.LOCATION.getName())

        testListOfBucketsFromMemory = Lists.newArrayList<BucketItem>(testBucketItem1, testBucketItem2)
        testListOfBucketsFromDisk = Lists.newArrayList<BucketItem>(testBucketItem1)
        testListOfBucketsFromNetwork = Lists.newArrayList<BucketItem>(testBucketItem1, testBucketItem2, null)
    }

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun loadBucketListFromMemoryTest() {
        doReturn(testListOfBucketsFromMemory).whenever(mockMemoryStorage)!!.get(any())
        checkLoadBucketList(Func1 { bucketListAction -> testListOfBucketsFromMemory!!.containsAll(bucketListAction.result) }, false)
    }

    @Test
    fun loadBucketListFromStorage() {
        `when`(mockDb!!.readBucketList(MOCK_USER_ID)).thenReturn(testListOfBucketsFromDisk)
        checkLoadBucketList(Func1 { bucketListAction -> testListOfBucketsFromDisk!!.containsAll(bucketListAction.result) }, false)
    }

    @Test
    fun loadBucketListFromNetwork() {
        checkLoadBucketList(Func1 { bucketListAction -> testListOfBucketsFromNetwork!!.containsAll(bucketListAction.result) }, false)
    }

    @Test
    fun forceLoadBucketListTest() {
        doReturn(testListOfBucketsFromMemory).whenever(mockMemoryStorage)!!.get(any())
        checkLoadBucketList(Func1 { bucketListAction -> testListOfBucketsFromNetwork!!.containsAll(bucketListAction.result) }, true)
    }

    @Test
    fun moveItemTest() {
        val POSITION_FROM = 0
        val POSITION_TO = 1

        val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
        val spyHttpCallback = spy(StubServiceWrapper.Callback::class.java)
        httpStubWrapper!!.callback = spyHttpCallback

        doReturn(testListOfBucketsFromMemory).whenever(mockMemoryStorage)!!.get(any())

        bucketInteractor!!.bucketListActionPipe()
                .createObservable(BucketListCommand.move(POSITION_FROM, POSITION_TO, BucketItem.BucketType.LOCATION))
                .subscribe(testSubscriber)

        assertActionSuccess(testSubscriber) { bucketListAction ->
            val items = bucketListAction.result
            testBucketItem1 == items[POSITION_TO] && testBucketItem2 == items[POSITION_FROM]
        }
    }

    override fun mockHttpService(): MockHttpActionService {
        return MockHttpActionService.Builder()
                .bind(MockHttpActionService.Response(200).body(testListOfBucketsFromNetwork)) {
                    it.url.contains("/bucket_list_items") && HttpAction.Method.GET.name == it.method
                }
                .bind(MockHttpActionService.Response(200).body(JsonObject())) {
                    it.url.contains("/position")
                }
                .build()
    }

    private fun checkLoadBucketList(predicate: Func1<BucketListCommand, Boolean>, force: Boolean) {
        val testSubscriber = TestSubscriber<ActionState<BucketListCommand>>()
        val spyHttpCallback = spy(StubServiceWrapper.Callback::class.java)
        httpStubWrapper!!.callback = spyHttpCallback

        bucketInteractor!!.bucketListActionPipe().createObservable(BucketListCommand.fetch(force)).subscribe(testSubscriber)
        assertActionSuccess(testSubscriber, predicate)
    }
}
