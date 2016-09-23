package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import javax.inject.Inject;

public class DtlVerifyAmountPresenter extends JobPresenter<DtlVerifyAmountPresenter.View> {

   @Inject DtlMerchantInteractor merchantInteractor;
   @Inject DtlTransactionInteractor transactionInteractor;
   //
   private final Merchant merchant;

   public DtlVerifyAmountPresenter(Merchant merchant) {
      this.merchant = merchant;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(transaction -> {
               view.attachTransaction(transaction, merchant.asMerchantAttributes().defaultCurrency());
               view.attachDtPoints(Double.valueOf(transaction.getPoints()).intValue());
            }, apiErrorPresenter::handleError);

   }

   public void rescan() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.get(merchant))
            .map(DtlTransactionAction::getResult)
            .doOnNext(transaction -> photoUploadingManagerS3.cancelUploading(transaction.getUploadTask()))
            .flatMap(transaction -> transactionInteractor.transactionActionPipe()
                  .createObservableResult(DtlTransactionAction.save(merchant, ImmutableDtlTransaction.copyOf(transaction)
                        .withUploadTask(null)))
                  .map(DtlTransactionAction::getResult))
            .compose(bindViewIoToMainComposer())
            .subscribe(view::openScanReceipt, apiErrorPresenter::handleError);
   }

   public void scanQr() {
      transactionInteractor.transactionActionPipe()
            .createObservableResult(DtlTransactionAction.update(merchant, transaction -> ImmutableDtlTransaction.copyOf(transaction)
                  .withIsVerified(true)))
            .map(DtlTransactionAction::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(view::openScanQr, apiErrorPresenter::handleError);
   }

   public interface View extends RxView {

      void attachDtPoints(int count);

      void attachTransaction(DtlTransaction dtlTransaction, Currency currency);

      void openScanReceipt(DtlTransaction dtlTransaction);

      void openScanQr(DtlTransaction dtlTransaction);
   }
}
