package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlVerifyAmountPresenter extends JobPresenter<DtlVerifyAmountPresenter.View> {

    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlTransactionService transactionService;
    //
    private final String merchantId;
    private DtlMerchant dtlMerchant;

    public DtlVerifyAmountPresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        merchantService.merchantByIdPipe()
                .createObservable(new DtlMerchantByIdAction(merchantId))
                .compose(ImmediateComposer.instance())
                .subscribe(new ActionStateSubscriber<DtlMerchantByIdAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> dtlMerchant = action.getResult()));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        transactionService.transactionActionPipe().createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> {
                    view.attachTransaction(transaction, dtlMerchant.getDefaultCurrency());
                    view.attachDtPoints(Double.valueOf(transaction.getPoints()).intValue());
                }, apiErrorPresenter::handleError);

    }

    public void rescan() {
        transactionService.transactionActionPipe()
                .createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .doOnNext(transaction ->
                        photoUploadingManagerS3.cancelUploading(transaction.getUploadTask())
                )
                .flatMap(transaction ->
                        transactionService.transactionActionPipe()
                                .createObservableResult(DtlTransactionAction.save(dtlMerchant,
                                        ImmutableDtlTransaction.copyOf(transaction).withUploadTask(null)))
                                .map(DtlTransactionAction::getResult)
                ).compose(bindViewIoToMainComposer())
                .subscribe(view::openScanReceipt, apiErrorPresenter::handleError);
    }

    public void scanQr() {
        transactionService.transactionActionPipe()
                .createObservableResult(DtlTransactionAction.update(dtlMerchant,
                        transaction -> ImmutableDtlTransaction.copyOf(transaction)
                                .withIsVerified(true)))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(view::openScanQr, apiErrorPresenter::handleError);
    }

    public interface View extends RxView {

        void attachDtPoints(int count);

        void attachTransaction(DtlTransaction dtlTransaction, DtlCurrency dtlCurrency);

        void openScanReceipt(DtlTransaction dtlTransaction);

        void openScanQr(DtlTransaction dtlTransaction);
    }
}
