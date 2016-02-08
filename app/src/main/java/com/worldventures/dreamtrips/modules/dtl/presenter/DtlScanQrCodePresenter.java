package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.UploadPurpose;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.event.DtlTransactionSucceedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantManager;

import javax.inject.Inject;

import rx.Observable;

public class DtlScanQrCodePresenter extends JobPresenter<DtlScanQrCodePresenter.View> {

    private final String merchantId;
    private DtlMerchant dtlMerchant;

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
        if (!TextUtils.isEmpty(dtlTransaction.getCode()))
            checkReceiptUploading();
        //
        photoUploadingManager.getTaskChangingObservable(UploadPurpose.DTL_RECEIPT).subscribe(uploadTask -> {
            if (dtlTransaction.getUploadTask().getId() == uploadTask.getId()) {
                switch (uploadTask.getStatus()) {
                    case COMPLETED:
                        dtlTransaction.getUploadTask().setOriginUrl(uploadTask.getOriginUrl());
                        onReceiptUploaded();
                        break;
                    case CANCELED:
                        break;
                    case STARTED:
                        break;
                    case FAILED:
                        receiptUploadError();
                        break;
                }
            }
        });
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
        dtlTransaction.setReceiptPhotoUrl(dtlTransaction.getUploadTask().getOriginUrl());
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
        photoUploadingManager.getUploadTasksObservable(UploadPurpose.DTL_RECEIPT)
                .flatMap(Observable::from)
                .first(uploadTask -> dtlTransaction.getUploadTask().getId() == uploadTask.getId())
                .subscribe(uploadTask -> {
                    switch (uploadTask.getStatus()) {
                        case COMPLETED:
                            onReceiptUploaded();
                            break;
                        case CANCELED:
                            break;
                        case STARTED:
                            view.showProgress(R.string.dtl_wait_for_receipt);
                            break;
                        case FAILED:
                            photoUploadingManager.upload(dtlTransaction.getUploadTask(), UploadPurpose.DTL_RECEIPT);
                            view.showProgress(R.string.dtl_wait_for_receipt);
                            break;
                    }
                });
    }

    private void receiptUploadError() {
        view.photoUploadError();
        view.hideProgress();
    }

    @Override
    public void dropView() {
        super.dropView();
    }

    public interface View extends RxView, ApiErrorView {
        void finish();

        void showProgress(@StringRes int titleRes);

        void hideProgress();

        void photoUploadError();

        void setMerchant(DtlMerchant DtlMerchant);

        void openScanReceipt(DtlTransaction dtlTransaction);
    }
}
