package com.worldventures.dreamtrips.modules.dtl.presenter;

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
   private static final String TRANSACTION_SUCCESSFUL = "THRST_TRANSACTION_SUCCESSFUL|";

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
      checkTransaction();
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
      String transactionResponse = action.getResult().transactionStatus();
      if (transactionResponse.equals(TRANSACTION_SUCCESSFUL)) {
         view.openThankYouScreen(action.getResult().billTotal(), action.getResult().pointsAmount(), action.getResult().pointsAmount());
      } else {
         view.openPaymentFailedScreen(action.getResult().billTotal(), action.getResult().pointsAmount(), action.getResult().pointsAmount());
      }
   }

   private void onMerchantsLoadingError(TransactionPilotAction action, Throwable throwable) {
      view.openPaymentFailedScreen(action.getErrorMessage(), action.getResult().pointsAmount(), action.getResult().pointsAmount());
   }

   public interface View extends RxView, ApiErrorView {
      void openThankYouScreen(String totalAmount, String earnedPoints, String totalPoints);

      void openPaymentFailedScreen(String totalAmount, String earnedPoints, String totalPoints);
   }
}
