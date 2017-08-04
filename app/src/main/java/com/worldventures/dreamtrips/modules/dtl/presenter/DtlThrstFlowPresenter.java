package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.messenger.util.StringUtils;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstFlowBundle;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.TransactionPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableTransactionThrstActionParams;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlThrstFlowPresenter extends JobPresenter<DtlThrstFlowPresenter.View> {
   private static final String TRANSACTION_FAILED = "THRST_TRANSACTION_FAILED";

   private static final String SUCCESS_STATUS = "200";

   @Inject MerchantsInteractor merchantInteractor;

   private ThrstFlowBundle thrstFlowBundle;

   public DtlThrstFlowPresenter(ThrstFlowBundle thrstFlowBundle) {
      this.thrstFlowBundle = thrstFlowBundle;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
   }

   public void onThrstCallback(JavaScriptInterface.ThrstStatusResponse thrstStatusResponse) {
      if (thrstStatusResponse.status.equals(SUCCESS_STATUS)) {
         checkTransaction();
      } else {
         view.openPaymentFailedScreen(thrstStatusResponse.body);
      }
   }

   private void checkTransaction() {
      ActionPipe<TransactionPilotAction> reviewActionPipe = merchantInteractor.transactionThrstHttpPipe();

      reviewActionPipe
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<TransactionPilotAction>()
                  .onSuccess(this::onMerchantsLoaded)
                  .onFail(this::onMerchantsLoadingError));

      reviewActionPipe.send(TransactionPilotAction.create(
            ImmutableTransactionThrstActionParams.builder()
                  .transactionId(thrstFlowBundle.getTransactionId())
                  .merchantId(thrstFlowBundle.getMerchant().id())
                  .build()));
   }

   private void onMerchantsLoaded(TransactionPilotAction action) {
      boolean isFail = StringUtils.containsIgnoreCase(action.getResult().transactionStatus(), TRANSACTION_FAILED);
      if (isFail) {
         view.openPaymentFailedScreen(action.getResult().billTotal());
      } else {
         view.openThankYouScreen(action.getResult().billTotal());
      }
   }

   private void onMerchantsLoadingError(TransactionPilotAction action, Throwable throwable) {
      view.openPaymentFailedScreen(action.getErrorMessage());
   }

   public interface View extends RxView, ApiErrorView {
      void openThankYouScreen(String totalAmount);

      void openPaymentFailedScreen(String totalAmount);
   }
}
