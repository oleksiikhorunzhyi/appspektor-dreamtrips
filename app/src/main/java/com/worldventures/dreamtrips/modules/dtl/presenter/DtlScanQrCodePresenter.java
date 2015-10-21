package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.EarnPointsRequest;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;

import javax.inject.Inject;

public class DtlScanQrCodePresenter extends Presenter<DtlScanQrCodePresenter.View> implements TransferListener {

    private final DtlPlace dtlPlace;
    private TransferObserver transferObserver;

    DtlTransaction dtlTransaction;

    @Inject
    SnappyRepository snapper;

    public DtlScanQrCodePresenter(DtlPlace dtlPlace) {
        this.dtlPlace = dtlPlace;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getId());
        view.setPlace(dtlPlace);

        if (!TextUtils.isEmpty(dtlTransaction.getCode()))
            checkReceiptUploading();
    }

    public void codeScanned(String content) {
        dtlTransaction.setCode(content);

        checkReceiptUploading();

        snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);
    }

    private void onReceiptUploaded() {
        view.showProgress(R.string.dtl_wait_for_earn);

        dtlTransaction.setReceiptPhoto(photoUploadingSpiceManager.
                getResultUrl(dtlTransaction.getUploadTask()));
        doRequest(new EarnPointsRequest(dtlPlace.getId(), dtlTransaction), this::processTransactionResult);
    }

    private void processTransactionResult(DtlTransactionResult result) {
        dtlTransaction.setDtlTransactionResult(result);
        snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);
        view.hideProgress();

        view.openTransactionSuccess(dtlPlace, dtlTransaction);
    }


    //////////////////////////////////////////////////
    /////////// Receipt uploading
    //////////////////////////////////////////////////

    private void checkReceiptUploading() {
        UploadTask uploadTask = dtlTransaction.getUploadTask();

        transferObserver =
                photoUploadingSpiceManager.getTransferById(uploadTask.getAmazonTaskId());

        switch (transferObserver.getState()) {
            case FAILED:
                //restart upload if failed
                transferObserver = photoUploadingSpiceManager.upload(dtlTransaction.getUploadTask());
                uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
                break;
            case COMPLETED:
                onReceiptUploaded();
                break;
        }

        transferObserver.setTransferListener(this);

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
        view.informUser(R.string.dtl_photo_upload_error);
        view.hideProgress();
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideProgress();
    }

    @Override
    public void dropView() {
        super.dropView();
        if (transferObserver != null) transferObserver.setTransferListener(null);
    }

    public interface View extends Presenter.View {
        void openTransactionSuccess(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void showProgress(@StringRes int titleRes);

        void hideProgress();

        void setPlace(DtlPlace dtlPlace);
    }
}
