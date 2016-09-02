package com.worldventures.dreamtrips.dtl.service.spek

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.dtl.merchants.EstimationHttpAction
import com.worldventures.dreamtrips.api.dtl.merchants.RatingHttpAction
import com.worldventures.dreamtrips.api.dtl.merchants.model.EstimationResult
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableEstimationParams
import com.worldventures.dreamtrips.api.dtl.merchants.requrest.ImmutableRatingParams
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.utils.DateTimeUtils
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEarnPointsAction
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import org.mockito.Mockito.eq
import org.powermock.api.mockito.PowerMockito.mockStatic
import org.powermock.core.classloader.annotations.PrepareForTest
import rx.observers.TestSubscriber

@PrepareForTest(DateTimeUtils::class)
class DtlTransactionInteractorSpec : BaseSpec({

   //general mocking for all tests
   merchant = mock()
   transaction = mock()
   transactionResult = mock()
   estimationResult = mock()

   whenever(merchant.id).thenReturn("test")
   whenever(merchant.defaultCurrency).thenReturn(Currency())
   whenever(transaction.asTransactionRequest(any())).thenReturn(mock())
   whenever(transaction.lat).thenReturn(1.toDouble())
   whenever(transaction.lng).thenReturn(1.toDouble())
   whenever(transaction.merchantToken).thenReturn("testToken")
   whenever(transaction.dtlTransactionResult).thenReturn(transactionResult)
   whenever(transactionResult.id).thenReturn("test")
   //

   beforeEach {
      val commandService = CommandActionService()
            .wrapCache()
            .wrapDagger()
      val janet = Janet.Builder().addService(commandService)
            .addService(MockHttpActionService.Builder()
                  .bind(MockHttpActionService
                        .Response(200)
                        .body(estimationResult)) { it.url.contains("/estimations") }
                  .bind(MockHttpActionService
                        .Response(200)
                        .body(transactionResult)) { it.url.contains("/transactions") }
                  .bind(MockHttpActionService.Response(200)) { it.url.contains("/ratings") }
                  .build()
                  .wrapCache())
            .build()

      db = spy()
      whenever(db.getDtlTransaction(any())).thenReturn(transaction)

      transactionInteractor = DtlTransactionInteractor(janet, janet)
      commandService.registerProvider(SnappyRepository::class.java) { db }
   }

   describe("DtlTransactionAction") {
      it("should get last transaction from db") {
         checkTransactionAction(DtlTransactionAction.get(merchant))
         { action -> action.result == transaction }
         //from cache
         checkTransactionAction(DtlTransactionAction.get(merchant))
         { action -> action.result == transaction }
         //
         verify(db).getDtlTransaction(anyString())
         verify(db, never()).saveDtlTransaction(anyString(), any())
      }
      it("should update transaction with custom func") {
         val merchantToken = "test"
         checkTransactionAction(DtlTransactionAction.update(merchant) {
            ImmutableDtlTransaction
                  .copyOf(it)
                  .withMerchantToken(merchantToken)
         }) { it.result.merchantToken == merchantToken }

         verify(db).getDtlTransaction(anyString())
         verify(db).saveDtlTransaction(anyString(), any())
      }
      it("should save new transaction") {
         val transaction = ImmutableDtlTransaction.copyOf(transaction)
               .withMerchantToken("test")
         checkTransactionAction(DtlTransactionAction.save(merchant, transaction)) { it.result == transaction }

         verify(db).getDtlTransaction(anyString())
         verify(db).saveDtlTransaction(anyString(), eq(transaction))
      }
      it("should clean last transaction") {
         checkTransactionAction(DtlTransactionAction.clean(merchant)) { it.result.merchantToken == null }

         verify(db).getDtlTransaction(anyString())
         verify(db).saveDtlTransaction(anyString(), any())
      }
      it("should delete last transaction") {
         checkTransactionAction(DtlTransactionAction.delete(merchant)) { it.result == null }

         verify(db).getDtlTransaction(anyString())
         verify(db, never()).saveDtlTransaction(anyString(), any())
         verify(db).deleteDtlTransaction(anyString())
      }
   }

   describe("EstimationHttpAction") {
      beforeEach {
         mockStatic(DateTimeUtils::class.java)
         whenever(DateTimeUtils.currentUtcString()).thenReturn("")
      }

      it("should send EstimationHttpAction") {
         val subscriber = TestSubscriber<ActionState<EstimationHttpAction>>()
         transactionInteractor.estimatePointsActionPipe()
               .createObservable(EstimationHttpAction(merchant.id,
                     ImmutableEstimationParams.builder()
                           .billTotal(Double.MAX_VALUE)
                           .checkinTime(DateTimeUtils.currentUtcString())
                           .currencyCode("USD")
                           .build()))
               .subscribe(subscriber)
         assertActionSuccess(subscriber) { it.estimatedPoints().points() != null }
      }
   }

   describe("RatingHttpAction") {
      it("should send RatingHttpAction") {
         val subscriber = TestSubscriber<ActionState<RatingHttpAction>>()
         transactionInteractor.rateActionPipe()
               .createObservable(RatingHttpAction(merchant.id,
                     ImmutableRatingParams.builder()
                           .rating(5)
                           .transactionId(transaction.dtlTransactionResult?.id)
                           .build()))
               .subscribe(subscriber)
         assertActionSuccess<RatingHttpAction>(subscriber) { it.errorResponse() == null }
      }
   }

   describe("DtlEarnPointsAction") {
      it("should send DtlEarnPointsAction") {
         val subscriber = TestSubscriber<ActionState<DtlEarnPointsAction>>()
         transactionInteractor.earnPointsActionPipe()
               .createObservable(DtlEarnPointsAction(merchant, transaction))
               .subscribe(subscriber)
         assertActionSuccess(subscriber) { it.result != null }
      }

   }
}) {
   companion object {
      //vars to ease use these in a constructor
      lateinit var transactionInteractor: DtlTransactionInteractor
      lateinit var db: SnappyRepository
      lateinit var merchant: DtlMerchant
      lateinit var transaction: DtlTransaction
      lateinit var estimationResult: EstimationResult
      lateinit var transactionResult: DtlTransactionResult

      //sugar method for sending and checking DtlTransactionAction using predicate
      fun checkTransactionAction(transactionAction: DtlTransactionAction, assertPredicate: (DtlTransactionAction) -> Boolean) {
         val subscriber = TestSubscriber<ActionState<DtlTransactionAction>>()
         transactionInteractor.transactionActionPipe().createObservable(transactionAction).subscribe(subscriber)
         assertActionSuccess(subscriber, { assertPredicate(transactionAction) })
      }
   }
}
