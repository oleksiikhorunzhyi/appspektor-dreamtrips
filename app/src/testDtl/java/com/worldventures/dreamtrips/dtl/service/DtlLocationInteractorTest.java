package com.worldventures.dreamtrips.dtl.service;


import android.location.Location;

import com.worldventures.dreamtrips.common.BaseTest;
import com.worldventures.dreamtrips.common.janet.MockDaggerActionService;
import com.worldventures.dreamtrips.common.janet.StubServiceWrapper;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlNearbyLocationAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlSearchLocationAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import io.techery.janet.http.test.MockHttpActionService;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.common.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DtlLocationInteractorTest extends BaseTest {

    private static final DtlExternalLocation testLocation;
    private static final List<DtlExternalLocation> testLocationList;

    static {
        testLocationList = Collections.singletonList(
                testLocation = mock(DtlExternalLocation.class)
        );
        when(testLocation.getLongName()).thenReturn("London");
    }

    private DtlLocationInteractor locationInteractor;
    private StubServiceWrapper httpStubWrapper;

    @Before
    public void setup() throws NoSuchFieldException {
        MockDaggerActionService daggerActionService;
        Janet janet = new Janet.Builder()
                .addService(cachedService(
                        daggerActionService = new MockDaggerActionService(new CommandActionService())))
                .addService(cachedService(
                        httpStubWrapper = new StubServiceWrapper(
                                new MockHttpActionService.Builder()
                                        .bind(new MockHttpActionService.Response(200).body(testLocationList),
                                                request -> request.getUrl().contains("/locations"))
                                        .build()))
                )
                .build();

        daggerActionService.registerProvider(Janet.class, () -> janet);

        locationInteractor = new DtlLocationInteractor(janet);
    }

    @Test
    public void testDtlLocationCommand() {
        TestSubscriber<ActionState<DtlLocationCommand>> subscriber = new TestSubscriber<>();

        locationInteractor.locationPipe()
                .createObservable(DtlLocationCommand.last())
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult().getLocationSourceType() == LocationSourceType.UNDEFINED);

        subscriber = new TestSubscriber<>();
        locationInteractor.locationPipe()
                .createObservable(DtlLocationCommand.change(testLocation))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult().equals(testLocation));

        subscriber = new TestSubscriber<>();
        locationInteractor.locationPipe()
                .createObservable(DtlLocationCommand.last())
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult().equals(testLocation));
    }

    @Test
    public void testDtlNearbyLocationAction() {
        TestSubscriber<ActionState<DtlNearbyLocationAction>> subscriber = new TestSubscriber<>();
        StubServiceWrapper.Callback spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        locationInteractor.nearbyLocationPipe()
                .createObservable(new DtlNearbyLocationAction(Mockito.mock(Location.class)))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty());
        verify(spyHttpCallback, times(1)).onSend(any(ActionHolder.class));
    }

    @Test
    public void testDtlSearchLocationAction() {
        TestSubscriber<ActionState<DtlSearchLocationAction>> subscriber = new TestSubscriber<>();
        StubServiceWrapper.Callback spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        locationInteractor.searchLocationPipe()
                .createObservable(new DtlSearchLocationAction(testLocation.getLongName().substring(0, 2)))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult().isEmpty());
        verify(spyHttpCallback, never()).onSend(any(ActionHolder.class));

        subscriber = new TestSubscriber<>();
        spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        locationInteractor.searchLocationPipe()
                .createObservable(new DtlSearchLocationAction(testLocation.getLongName().substring(0, 3)))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty());
        verify(spyHttpCallback, times(1)).onSend(any(ActionHolder.class));

        subscriber = new TestSubscriber<>();
        spyHttpCallback = spy(StubServiceWrapper.Callback.class);
        httpStubWrapper.setCallback(spyHttpCallback);
        locationInteractor.searchLocationPipe()
                .createObservable(new DtlSearchLocationAction(testLocation.getLongName()))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> !action.getResult().isEmpty());
        verify(spyHttpCallback, never()).onSend(any(ActionHolder.class));
    }
}
