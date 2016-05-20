package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.action.DtlEarnPointsAction;
import com.worldventures.dreamtrips.modules.dtl.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.store.DtlTransactionService;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlScanQrCodePresenter extends JobPresenter<DtlScanQrCodePresenter.View> implements TransferListener {

    @Inject
    DtlMerchantService merchantStore;
    @Inject
    DtlTransactionService transactionService;
    //
    private final String merchantId;
    private DtlMerchant dtlMerchant;
    private TransferObserver transferObserver;

    public DtlScanQrCodePresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        merchantStore.getMerchantById(merchantId)
                .compose(ImmediateComposer.instance())
                .subscribe(merchant -> dtlMerchant = merchant);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        //
        view.setMerchant(dtlMerchant);
        //
        transactionService.transactionActionPipe().createObservable(DtlTransactionAction.get(dtlMerchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            DtlTransaction transaction = action.getResult();
                            if (transaction != null && transaction.isMerchantCodeScanned()) {
                                checkReceiptUploading(transaction);
                            }
                        }));
        //
        bindApiJob();
    }

    private void bindApiJob() {
        transactionService.earnPointsActionPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlEarnPointsAction>()
                        .onStart(action -> view.showProgress(R.string.dtl_wait_for_earn))
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(this::processTransactionResult));
    }

    public void codeScanned(String scannedQr) {
        TrackingHelper.dtlScanMerchant(scannedQr);
        transactionService.transactionActionPipe().createObservable(DtlTransactionAction.get(dtlMerchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            if (action.getResult() != null) {
                                DtlTransaction dtlTransaction = action.getResult();
                                dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                                        .withMerchantToken(scannedQr);
                                transactionService.transactionActionPipe().send(DtlTransactionAction.save(dtlMerchant, dtlTransaction));
                                checkReceiptUploading(dtlTransaction);
                            }
                        }));

        //

    }

    public void photoUploadFailed() {
        transactionService.transactionActionPipe().createObservable(DtlTransactionAction.clean(dtlMerchant))
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> {
                            if (action.getResult() != null) {
                                if (view != null) view.openScanReceipt(action.getResult());
                            }
                        }));

    }

    private void onReceiptUploaded() {
        transactionService.transactionActionPipe().createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                .map(CommandActionBase::getResult)
                .map(transaction -> ImmutableDtlTransaction.copyOf(transaction)
                        .withReceiptPhotoUrl(photoUploadingManagerS3.getResultUrl(transaction.getUploadTask())))
                .subscribe(dtlTransaction -> transactionService.earnPointsActionPipe().send(
                        new DtlEarnPointsAction(dtlMerchant, dtlTransaction)
                ), apiErrorPresenter::handleError);

    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        transactionService.transactionActionPipe().createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .map(transaction -> ImmutableDtlTransaction.copyOf(transaction).withMerchantToken(null))
                .flatMap(transaction ->
                        transactionService.transactionActionPipe()
                                .createObservableSuccess(DtlTransactionAction.save(dtlMerchant, transaction))
                )
                .compose(bindViewIoToMainComposer())
                .subscribe(action -> {
                }, apiErrorPresenter::handleError);
    }

    private void processTransactionResult(DtlEarnPointsAction action) {
        TrackingHelper.dtlPointsEarned(Double.valueOf(action.getResult().getEarnedPoints()).intValue());
        view.hideProgress();
        //
        transactionService.transactionActionPipe()
                .send(DtlTransactionAction.save(action.getMerchant(),
                        ImmutableDtlTransaction.copyOf(action.getTransaction())
                                .withDtlTransactionResult(action.getResult())));
        ;
        //
        eventBus.postSticky(new DtlTransactionSucceedEvent(action.getTransaction()));
        view.finish();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Receipt uploading
    ///////////////////////////////////////////////////////////////////////////

    private void checkReceiptUploading(DtlTransaction transaction) {
        UploadTask uploadTask = transaction.getUploadTask();
        //
        transferObserver =
                photoUploadingManagerS3.getTransferById(uploadTask.getAmazonTaskId());
        //
        switch (transferObserver.getState()) {
            case FAILED:
                //restart upload if failed
                transferObserver = photoUploadingManagerS3.upload(transaction.getUploadTask());
                uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
                setListener();
                break;
            case IN_PROGRESS:
                setListener();
                break;
            case COMPLETED:
                onReceiptUploaded();
                break;
            case WAITING_FOR_NETWORK:
                view.noConnection();
                break;
        }
    }

    private void setListener() {
        transferObserver.setTransferListener(this);
        //
        view.showProgress(R.string.dtl_wait_for_receipt);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        transactionService.transactionActionPipe()
                .createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .filter(transaction -> Integer.valueOf(transaction.getUploadTask().getAmazonTaskId()) == id)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> {
                    switch (state) {
                        case COMPLETED:
                            onReceiptUploaded();
                            break;
                        case FAILED:
                            receiptUploadError();
                            break;
                        case WAITING_FOR_NETWORK:
                            view.noConnection();
                    }
                }, apiErrorPresenter::handleError);
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        //nothing to do here
    }

    @Override
    public void onError(int id, Exception ex) {
        receiptUploadError();
    }

    private void receiptUploadError() {
        view.photoUploadError();
        view.hideProgress();
    }

    @Override
    public void dropView() {
        super.dropView();
        if (transferObserver != null) transferObserver.setTransferListener(null);
    }

    public interface View extends RxView, ApiErrorView {
        void finish();

        void showProgress(@StringRes int titleRes);

        void hideProgress();

        void photoUploadError();

        void noConnection();

        void setMerchant(DtlMerchant DtlMerchant);

        void openScanReceipt(DtlTransaction dtlTransaction);
    }
}
