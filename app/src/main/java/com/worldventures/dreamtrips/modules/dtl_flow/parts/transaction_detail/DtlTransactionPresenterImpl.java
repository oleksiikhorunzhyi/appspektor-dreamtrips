package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionPresenterImpl extends DtlPresenterImpl<DtlTransactionScreen, ViewState.EMPTY> implements DtlTransactionPresenter {
   @Inject FullMerchantInteractor merchantsInteractor;
   private TransactionModel transaction;

   public DtlTransactionPresenterImpl(Context context, Injector injector, TransactionModel transaction) {
      super(context);
      injector.inject(this);
      this.transaction = transaction;
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (transaction.isTrhstTransaction()) {
         getView().showThrstTransaction(transaction);
      } else {
         getView().showNonThrstTransaction(transaction);
      }
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
   }

   @Override
   public void onBackPressed() {

   }

   @Override
   public void showReceipt() {
      if (transaction.getReceiptUrl() != null) {
         getView().showReceipt(transaction.getReceiptUrl());
      }
   }

   @Override
   public void reviewMerchant() {
      getView().showLoadingMerchantDialog();
      merchantsInteractor.load(transaction.getMerchantId());
      merchantsInteractor.fullMerchantPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FullMerchantAction>()
                  .onSuccess(action -> {
                     getView().hideLoadingMerchantDialog();
                     Flow.get(getContext()).set(new DtlCommentReviewPath(action.getResult()));
                  })
                  .onFail((fullMerchantAction, throwable) -> {
                     getView().hideLoadingMerchantDialog();
                     getView().showCouldNotShowMerchantDialog();
                  }));
   }
}