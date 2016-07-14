package com.worldventures.dreamtrips.social;

import com.google.gson.JsonObject;
import com.worldventures.dreamtrips.core.test.MockHttpActionService;
import com.worldventures.dreamtrips.core.test.StubServiceWrapper;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import io.techery.janet.ActionState;
import io.techery.janet.http.annotations.HttpAction;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class BucketListInteractorTest extends BucketInteractorBaseTest {
    private List<BucketItem> testListOfBucketsFromMemory;
    private List<BucketItem> testListOfBucketsFromDisk;
    private List<BucketItem> testListOfBucketsFromNetwork;

    private BucketItem testBucketItem1;
    private BucketItem testBucketItem2;

    {
        testBucketItem1 = mock(BucketItem.class);
        testBucketItem2 = mock(BucketItem.class);
        when(testBucketItem1.getUid()).thenReturn("1");
        when(testBucketItem2.getUid()).thenReturn("2");

        when(testBucketItem1.getType()).thenReturn(BucketItem.BucketType.LOCATION.getName());
        when(testBucketItem2.getType()).thenReturn(BucketItem.BucketType.LOCATION.getName());

        testListOfBucketsFromMemory = Lists.newArrayList(testBucketItem1, testBucketItem2);
        testListOfBucketsFromDisk = Lists.newArrayList(testBucketItem1);
        testListOfBucketsFromNetwork = Lists.newArrayList(testBucketItem1, testBucketItem2, null);
    }

    @Before
    public void setup() throws IOException, URISyntaxException {
        super.setup();
    }

    @Test
    public void loadBucketListFromMemoryTest() {
        when(mockMemoryStorage.get(null))
                .thenReturn(testListOfBucketsFromMemory);
        checkLoadBucketList(bucketListAction -> testListOfBucketsFromMemory.containsAll(bucketListAction.getResult()), false);
        verify(mockMemoryStorage).get(any());
    }

    @Test
    public void loadBucketListFromStorage() {
        when(mockDb.readBucketList(MOCK_USER_ID))
                .thenReturn(testListOfBucketsFromDisk);
        checkLoadBucketList(bucketListAction -> testListOfBucketsFromDisk.containsAll(bucketListAction.getResult()), false);
        verify(mockDb).readBucketList(anyInt());
    }

    @Test
    public void loadBucketListFromNetwork() {
        checkLoadBucketList(bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.getResult()), false);
    }

    @Test
    public void forceLoadBucketListTest() {
        when(mockMemoryStorage.get(null))
                .thenReturn(testListOfBucketsFromMemory);
        checkLoadBucketList(bucketListAction -> testListOfBucketsFromNetwork.containsAll(bucketListAction.getResult()), true);
        verify(mockMemoryStorage).get(any());
    }

    @Test
    public void moveItemTest() {
        final int POSITION_FROM = 0;
        final int POSITION_TO = 1;

        TestSubscriber<ActionState<BucketListCommand>> testSubscriber = new TestSubscriber<>();
        StubServiceWrapper.Callback spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);

        when(mockMemoryStorage.get(null))
                .thenReturn(testListOfBucketsFromMemory);

        bucketInteractor.bucketListActionPipe()
                .createObservable(BucketListCommand.move(POSITION_FROM, POSITION_TO, BucketItem.BucketType.LOCATION))
                .subscribe(testSubscriber);

        assertActionSuccess(testSubscriber, bucketListAction -> {
            List<BucketItem> items = bucketListAction.getResult();
            return testBucketItem1.equals(items.get(POSITION_TO))
                    && testBucketItem2.equals(items.get(POSITION_FROM));
        });
        verify(mockMemoryStorage).get(any());
    }

    @Override
    protected MockHttpActionService mockHttpService() {
        return new MockHttpActionService.Builder()
                .bind(new MockHttpActionService.Response(200).body(testListOfBucketsFromNetwork),
                        request -> request.getUrl().contains("/bucket_list_items")
                                && HttpAction.Method.GET.name().equals(request.getMethod()))
                .bind(new MockHttpActionService.Response(200).body(new JsonObject()),
                        request -> request.getUrl().contains("/position"))
                .build();
    }

    private void checkLoadBucketList(Func1<BucketListCommand, Boolean> predicate, boolean force) {
        TestSubscriber<ActionState<BucketListCommand>> testSubscriber = new TestSubscriber<>();
        StubServiceWrapper.Callback spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);

        bucketInteractor.bucketListActionPipe()
                .createObservable(BucketListCommand.fetch(force))
                .subscribe(testSubscriber);
        assertActionSuccess(testSubscriber, predicate);
    }
}