package com.worldventures.dreamtrips.dtl.service.junit;

import android.location.Location;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlUpdateAmenitiesAction;

import org.junit.Before;
import org.junit.Test;

import io.techery.janet.ActionState;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DtlMerchantInteractorTest extends DtlBaseMerchantServiceTest {

    @Before
    public void setup() {
        init();
    }

    @Test
    public void testDtlMerchantsAction() {
        checkDtlMerchantsAction();
    }

    @Test
    public void testUpdateAmenities() {
        TestSubscriber<ActionState<DtlUpdateAmenitiesAction>> subscriber = new TestSubscriber<>();
        janet.createPipe(DtlUpdateAmenitiesAction.class).observe().subscribe(subscriber);

        checkDtlMerchantsAction();

        subscriber.unsubscribe();
        assertActionSuccess(subscriber, action -> action.getResult() != null);
        verify(db, times(1)).saveAmenities(anyCollectionOf(DtlMerchantAttribute.class));
    }

    @Test
    public void testFilterChange() {
        TestSubscriber<ActionState<DtlFilterDataAction>> subscriber = new TestSubscriber<>();
        janet.createPipe(DtlFilterDataAction.class).observe().subscribe(subscriber);

        checkDtlMerchantsAction();

        subscriber.unsubscribe();
        assertActionSuccess(subscriber, action -> action.getResult() != null);
    }

    @Test
    public void testLocationChange() {
        TestSubscriber<ActionState<DtlLocationCommand>> subscriber = new TestSubscriber<>();
        janet.createPipe(DtlLocationCommand.class).observe().subscribe(subscriber);

        checkDtlMerchantsAction();

        subscriber.unsubscribe();
        assertActionSuccess(subscriber, action -> action.getResult() != null);
    }

    @Test
    public void testDtlMerchantByIdAction() {
        merchantInteractor.merchantsActionPipe()
                .send(DtlMerchantsAction.load(mock(Location.class)));

        TestSubscriber<ActionState<DtlMerchantByIdAction>> subscriber = new TestSubscriber<>();
        merchantInteractor.merchantByIdPipe()
                .createObservable(new DtlMerchantByIdAction(MERCHANT_ID))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult() != null);
    }
}
