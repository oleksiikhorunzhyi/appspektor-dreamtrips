package com.worldventures.dreamtrips.dtl.service;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.test.BaseTest;
import com.worldventures.dreamtrips.core.test.MockDaggerActionService;
import com.worldventures.dreamtrips.core.test.MockHttpActionService;
import com.worldventures.dreamtrips.core.test.StubServiceWrapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Collections;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DtlBaseMerchantServiceTest extends BaseTest {

    protected static final String MERCHANT_ID = "test";

    private static final DtlMerchant testMerchant;
    private static final List<DtlMerchant> testMerchantList;

    static {
        testMerchantList = Collections.singletonList(
                testMerchant = mock(DtlMerchant.class)
        );
        when(testMerchant.getId()).thenReturn(MERCHANT_ID);
        when(testMerchant.getCoordinates()).thenReturn(new Location(1, 1));
        when(testMerchant.getAnalyticsName()).thenReturn("test");
        when(testMerchant.getAmenities()).thenReturn(Collections.emptyList());
        when(testMerchant.getBudget()).thenReturn(3);
        when(testMerchant.getDisplayName()).thenReturn("test");
        when(testMerchant.getMerchantType()).thenReturn(DtlMerchantType.DINING);
    }

    protected DtlMerchantInteractor merchantInteractor;
    protected StubServiceWrapper httpStubWrapper;
    protected DtlLocationInteractor locationInteractor;
    protected Janet janet;
    protected SnappyRepository db;

    protected void init() {
        MockDaggerActionService daggerActionService;
        janet = new Janet.Builder()
                .addService(cachedService(
                        daggerActionService = new MockDaggerActionService(new CommandActionService())))
                .addService(cachedService(
                        httpStubWrapper = new StubServiceWrapper(
                                new MockHttpActionService.Builder()
                                        .bind(new MockHttpActionService.Response(200).body(testMerchantList),
                                                request -> request.getUrl().contains("/merchants"))
                                        .build()))
                )
                .build();

        locationInteractor = new DtlLocationInteractor(janet);
        merchantInteractor = new DtlMerchantInteractor(janet, locationInteractor);
        db = spy(SnappyRepository.class);

        daggerActionService.registerProvider(Janet.class, () -> janet);
        daggerActionService.registerProvider(DtlMerchantInteractor.class, () -> merchantInteractor);
        daggerActionService.registerProvider(SnappyRepository.class, () -> db);
    }

    protected void checkDtlMerchantsAction() {
        TestSubscriber<ActionState<DtlMerchantsAction>> subscriber = new TestSubscriber<>();
        StubServiceWrapper.Callback spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        merchantInteractor.merchantsActionPipe()
                .createObservable(DtlMerchantsAction.load(mock(android.location.Location.class)))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty() && action.isFromApi());
        verify(spyHttpCallback, times(1)).onSend(any(ActionHolder.class));

        subscriber = new TestSubscriber<>();
        spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        merchantInteractor.merchantsActionPipe()
                .createObservable(DtlMerchantsAction.restore())
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty() && !action.isFromApi());
        verify(spyHttpCallback, never()).onSend(any(ActionHolder.class));
    }

}
