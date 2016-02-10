package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

public class DtlScanQrCodePresenter extends JobPresenter<DtlScanQrCodePresenter.View> implements TransferListener {

    private final String merchantId;
    private DtlMerchant dtlMerchant;
    private TransferObserver transferObserver;

    DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;
    @Inject
    DtlMerchantManager dtlMerchantManager;
    @Inject
    DtlJobManager jobManager;

    public DtlScanQrCodePresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchant = dtlMerchantManager.getMerchantById(merchantId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        //
        dtlTransaction = snapper.getDtlTransaction(dtlMerchant.getId());
        view.setMerchant(dtlMerchant);
        //
        if (dtlTransaction.merchantCodeScanned())
            checkReceiptUploading();
        //
        bindApiJob();
    }

    private void bindApiJob() {
        bindJobCached(jobManager.earnPointsExecutor)
                .onProgress(() -> view.showProgress(R.string.dtl_wait_for_earn))
                .onError(apiErrorPresenter::handleError)
                .onSuccess(this::processTransactionResult);
    }

    public void codeScanned(String content) {
        TrackingHelper.dtlScanMerchant(content);
        dtlTransaction.setCode(content);
        snapper.saveDtlTransaction(dtlMerchant.getId(), dtlTransaction);
        //
        checkReceiptUploading();
    }

    public void photoUploadFailed() {
        snapper.cleanDtlTransaction(dtlMerchant.getId(), dtlTransaction);
        //
        if (view != null)
            view.openScanReceipt(dtlTransaction);
    }

    private void onReceiptUploaded() {
        dtlTransaction.setReceiptPhotoUrl(photoUploadingManagerS3.
                getResultUrl(dtlTransaction.getUploadTask()));
        jobManager.earnPointsExecutor.createJobWith(dtlMerchant.getId(), dtlMerchant.getDefaultCurrency().getCode(),
                dtlTransaction).subscribe();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        dtlTransaction.setCode(null);
        snapper.saveDtlTransaction(dtlMerchant.getId(), dtlTransaction);
    }

    private void processTransactionResult(DtlTransactionResult result) {
        TrackingHelper.dtlPointsEarned(Double.valueOf(result.getEarnedPoints()).intValue());
        view.hideProgress();
        //
        dtlTransaction.setDtlTransactionResult(result);
        snapper.saveDtlTransaction(dtlMerchant.getId(), dtlTransaction);
        //
        eventBus.postSticky(new DtlTransactionSucceedEvent(dtlTransaction));
        view.finish();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Receipt uploading
    ///////////////////////////////////////////////////////////////////////////

    private void checkReceiptUploading() {
        UploadTask uploadTask = dtlTransaction.getUploadTask();
        //
        transferObserver =
                photoUploadingManagerS3.getTransferById(uploadTask.getAmazonTaskId());
        //
        switch (transferObserver.getState()) {
            case FAILED:
                //restart upload if failed
                transferObserver = photoUploadingManagerS3.upload(dtlTransaction.getUploadTask());
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
        if (Integer.valueOf(dtlTransaction.getUploadTask().getAmazonTaskId()) == id) {
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
        }
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
