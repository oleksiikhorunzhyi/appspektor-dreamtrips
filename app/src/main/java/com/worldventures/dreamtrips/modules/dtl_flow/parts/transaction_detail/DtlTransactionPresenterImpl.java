package com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail;

import android.content.Context;
import android.view.View;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FullMerchantAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.SendEmailAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TakeScreenshotAction;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlPresenterImpl;
import com.worldventures.dreamtrips.modules.dtl_flow.ViewState;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.comment.DtlCommentReviewPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionPresenterImpl extends DtlPresenterImpl<DtlTransactionScreen, ViewState.EMPTY>
      implements DtlTransactionPresenter {

   @Inject FullMerchantInteractor merchantsInteractor;
   @Inject MerchantsInteractor merchantInteractor;

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
   public void onSendEmailClick(View screenshotView) {
      merchantInteractor.takeScreenshotPipe()
            .createObservable(new TakeScreenshotAction(screenshotView))
            .compose(bindViewIoToMainComposer())
            .filter(takeScreenshotActionState -> takeScreenshotActionState.action.getResult() != null)
            .subscribe(new ActionStateSubscriber<TakeScreenshotAction>()
                  .onStart(takeScreenshotAction -> getView().hideTransactionButtons())
                  .onSuccess(takeScreenshotAction -> sendEmail(takeScreenshotAction.getResult()))
                  .onFinish(takeScreenshotAction -> getView().showTransactionButtons()));
   }

   private void sendEmail(String path) {
      merchantInteractor.sendEmailPipe().createObservable(
            new SendEmailAction(
                  transaction.getMerchantId(),
                  transaction.getId(),
                  path))
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SendEmailAction>()
                  .onStart(sendEmailAction -> getView().showLoading())
                  .onSuccess(sendEmailAction -> getView().showSuccessEmailMessage())
                  .onFail((sendEmailAction, throwable) -> getView().showErrorEmailMessage())
                  .onFinish(sendEmailAction -> getView().hideLoading()));
   }

   @Override
   public void showReceipt() {
      if (transaction.getReceiptUrl() != null) {
         getView().showReceipt(transaction.getReceiptUrl());
      }
   }

   @Override
   public void reviewMerchant() {
      merchantsInteractor.fullMerchantPipe().observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<FullMerchantAction>()
                  .onStart(fullMerchantAction -> getView().showLoading())
                  .onSuccess(action -> Flow.get(getContext()).set(new DtlCommentReviewPath(action.getResult())))
                  .onFail((fullMerchantAction, throwable) -> getView().showCouldNotShowMerchantDialog())
                  .onFinish(fullMerchantAction -> getView().hideLoading()));
      merchantsInteractor.load(transaction.getMerchantId());
   }
}
