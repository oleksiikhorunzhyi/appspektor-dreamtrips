package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.test.BaseTest;
import com.worldventures.dreamtrips.core.test.MockDaggerActionService;
import com.worldventures.dreamtrips.core.test.MockHttpActionService;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl.model.EstimationPointsHolder;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlRateAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import io.techery.janet.ActionState;
import io.techery.janet.CommandActionService;
import io.techery.janet.Janet;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static com.worldventures.dreamtrips.core.test.AssertUtil.assertActionSuccess;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest(DateTimeUtils.class)
public class DtlTransactionInteractorTest extends BaseTest {

    private static DtlMerchant testMerchant = mock(DtlMerchant.class);
    private static DtlTransaction testTransaction = mock(DtlTransaction.class);
    private static EstimationPointsHolder testPointsHolder = mock(EstimationPointsHolder.class);
    private static DtlTransactionResult testTransactionResult = mock(DtlTransactionResult.class);

    static {
        when(testMerchant.getId()).thenReturn("test");
        when(testMerchant.getDefaultCurrency()).thenReturn(new DtlCurrency());
        when(testTransactionResult.getId()).thenReturn("test");
        when(testTransaction.getLat()).thenReturn(Double.valueOf(1));
        when(testTransaction.getLng()).thenReturn(Double.valueOf(1));
        when(testTransaction.getMerchantToken()).thenReturn("testToken");
        when(testTransaction.getDtlTransactionResult()).thenReturn(testTransactionResult);
        when(testTransaction.asTransactionRequest(anyString())).thenReturn(mock(DtlTransaction.Request.class));
    }

    private DtlTransactionInteractor transactionInteractor;
    private SnappyRepository db;

    @Before
    public void setup() throws Exception {
        MockDaggerActionService daggerActionService;
        Janet janet = new Janet.Builder()
                .addService(cachedService(
                        daggerActionService = new MockDaggerActionService(new CommandActionService())))
                .addService(cachedService(
                        new MockHttpActionService.Builder()
                                .bind(new MockHttpActionService.Response(200).body(testPointsHolder),
                                        request -> request.getUrl().contains("/estimations"))
                                .bind(new MockHttpActionService.Response(200).body(testTransactionResult),
                                        request -> request.getUrl().contains("/transactions"))
                                .bind(new MockHttpActionService.Response(200),
                                        request -> request.getUrl().contains("/ratings"))
                                .build()))
                .build();

        transactionInteractor = new DtlTransactionInteractor(janet);

        db = spy(SnappyRepository.class);
        when(db.getDtlTransaction(anyString())).thenReturn(testTransaction);
        PowerMockito.mockStatic(DateTimeUtils.class);
        when(DateTimeUtils.currentUtcString()).thenReturn("");

        daggerActionService.registerProvider(SnappyRepository.class, () -> db);
    }

    @Test
    public void testTransactionGet() {
        checkTransactionAction(DtlTransactionAction.get(testMerchant), action -> action.getResult().equals(testTransaction));
        //from cache
        checkTransactionAction(DtlTransactionAction.get(testMerchant), action -> action.getResult().equals(testTransaction));
        //
        verify(db, times(1)).getDtlTransaction(anyString());
        verify(db, never()).saveDtlTransaction(anyString(), any(DtlTransaction.class));
    }

    @Test
    public void testTransactionUpdate() {
        String merchantToken = "test";
        checkTransactionAction(DtlTransactionAction.update(testMerchant,
                transaction -> ImmutableDtlTransaction.copyOf(transaction)
                        .withMerchantToken(merchantToken)),
                action -> action.getResult().getMerchantToken().equals(merchantToken));
        verify(db, times(1)).getDtlTransaction(anyString());
        verify(db, times(1)).saveDtlTransaction(anyString(), any(DtlTransaction.class));
    }

    @Test
    public void testTransactionSave() {
        DtlTransaction transaction = ImmutableDtlTransaction.copyOf(testTransaction)
                .withMerchantToken("test");
        checkTransactionAction(DtlTransactionAction.save(testMerchant, transaction), action -> action.getResult().equals(transaction));
        verify(db, times(1)).getDtlTransaction(anyString());
        verify(db, times(1)).saveDtlTransaction(anyString(), eq(transaction));
    }

    @Test
    public void testTransactionClean() {
        checkTransactionAction(DtlTransactionAction.clean(testMerchant), action -> action.getResult().getMerchantToken() == null);
        verify(db, times(1)).getDtlTransaction(anyString());
        verify(db, times(1)).saveDtlTransaction(anyString(), any(DtlTransaction.class));
    }

    @Test
    public void testTransactionDelete() {
        checkTransactionAction(DtlTransactionAction.delete(testMerchant), action -> action.getResult() == null);
        verify(db, times(1)).getDtlTransaction(anyString());
        verify(db, never()).saveDtlTransaction(anyString(), any(DtlTransaction.class));
        verify(db, times(1)).deleteDtlTransaction(anyString());
    }

    @Test
    public void testDtlEstimatePointsAction() {
        TestSubscriber<ActionState<DtlEstimatePointsAction>> subscriber = new TestSubscriber<>();
        transactionInteractor.estimatePointsActionPipe()
                .createObservable(new DtlEstimatePointsAction(testMerchant, Double.MAX_VALUE, ""))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getEstimationPointsHolder() != null);
    }

    @Test
    public void testDtlRateAction() {
        TestSubscriber<ActionState<DtlRateAction>> subscriber = new TestSubscriber<>();
        transactionInteractor.rateActionPipe()
                .createObservable(new DtlRateAction(testMerchant, 5, testTransaction))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getErrorResponse() == null);
    }

    @Test
    public void testDtlEarnPointsAction() {
        TestSubscriber<ActionState<DtlEarnPointsAction>> subscriber = new TestSubscriber<>();
        transactionInteractor.earnPointsActionPipe()
                .createObservable(new DtlEarnPointsAction(testMerchant, testTransaction))
                .subscribe(subscriber);
        assertActionSuccess(subscriber, action -> action.getResult() != null);
    }

    private void checkTransactionAction(DtlTransactionAction transactionAction, Func1<DtlTransactionAction, Boolean> assertPredicate) {
        TestSubscriber<ActionState<DtlTransactionAction>> subscriber = new TestSubscriber<>();
        transactionInteractor.transactionActionPipe()
                .createObservable(transactionAction)
                .subscribe(subscriber);
        assertActionSuccess(subscriber, assertPredicate);
    }
}