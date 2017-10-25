package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.esotericsoftware.minlog.Log;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.bundle.ThrstFlowBundle;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.TransactionPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableTransactionThrstActionParams;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlThrstFlowPresenter extends JobPresenter<DtlThrstFlowPresenter.View> {
   private static final String TRANSACTION_SUCCESSFUL = "THRST_TRANSACTION_SUCCESSFUL";

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

      GetTransactionResponse response = action.getResult();

      Log.debug("XYZ", response.subTotal());
      Log.debug("XYZ", response.tip());
      Log.debug("XYZ", response.tax());

      if (transactionResponse.contains(TRANSACTION_SUCCESSFUL)) {
//         view.openThankYouScreen(action.getResult().billTotal(), action.getResult().pointsAmount(), action.getResult().pointsAmount(), action.getResult().billImagePath());
         view.openThankYouScreen(response);
      } else {
         view.openPaymentFailedScreen(response);
      }
   }

   private void onMerchantsLoadingError(TransactionPilotAction action, Throwable throwable) {
      view.openPaymentFailedScreen(action.getResult());
   }

   public interface View extends RxView {
      void openThankYouScreen(GetTransactionResponse transactionResponse);

      void openPaymentFailedScreen(GetTransactionResponse transactionResponse);
   }
}
