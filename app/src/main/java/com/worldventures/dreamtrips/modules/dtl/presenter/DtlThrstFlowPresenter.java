package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.TransactionPilotAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableTransactionThrstActionParams;
import com.worldventures.dreamtrips.modules.dtl.view.custom.webview.javascript.JavaScriptInterface;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlThrstFlowPresenter extends Presenter<DtlThrstFlowPresenter.View> {

   @Inject MerchantsInteractor merchantInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;

   private Merchant merchant;

   public DtlThrstFlowPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(dtlTransactionAction -> view.setupThrstPage(merchant.displayName(),
                  dtlTransactionAction.getResult().getUrlTokenResponse().thrstInfo().token(),
                  dtlTransactionAction.getResult().getUrlTokenResponse().thrstInfo().redirectUrl()));
   }

   public void onThrstCallback(JavaScriptInterface.ThrstStatusResponse thrstStatusResponse) {
      checkTransaction();
   }

   private void checkTransaction() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .compose(bindViewToMainComposer())
            .subscribe(dtlTransactionAction -> merchantInteractor.transactionThrstHttpPipe()
                  .createObservable(TransactionPilotAction.create(
                        ImmutableTransactionThrstActionParams.builder()
                              .transactionId(dtlTransactionAction.getResult()
                                    .getUrlTokenResponse()
                                    .transaction()
                                    .transactionId())
                              .merchantId(merchant.id())
                              .build()))
                  .compose(bindViewToMainComposer())
                  .subscribe(new ActionStateSubscriber<TransactionPilotAction>()
                        .onSuccess(this::onTransactionDetailsLoaded)
                        .onFail((transactionPilotAction, throwable) -> view.openThankYouScreen(merchant))));
   }

   private void onTransactionDetailsLoaded(TransactionPilotAction action) {
      transactionInteractor.transactionActionPipe().send(DtlTransactionAction.update(merchant,
            dtlTransaction -> ImmutableDtlTransaction.copyOf(dtlTransaction)
                  .withTransactionResponse(action.getResult())));
      view.openThankYouScreen(merchant);
   }

   public interface View extends RxView {
      void setupThrstPage(String merchantName, String token, String receiptUrl);

      void openThankYouScreen(Merchant merchant);

   }
}
