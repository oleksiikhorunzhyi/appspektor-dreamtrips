package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.util.ValidationUtils;

import java.io.File;

import javax.inject.Inject;

import icepick.State;

public class DtlScanReceiptPresenter extends Presenter<DtlScanReceiptPresenter.View> implements TransferListener {

    public static final int REQUESTER_ID = -3;

    private final DtlPlace dtlPlace;

    @State
    String amount;

    @Inject
    SnappyRepository snapper;

    private DtlTransaction dtlTransaction;

    public DtlScanReceiptPresenter(DtlPlace place) {
        this.dtlPlace = place;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);

        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getId());

        if (!TextUtils.isEmpty(dtlTransaction.getReceiptPhoto())) {
            view.hideScanButton();
            view.attachReceipt(Uri.parse(dtlTransaction.getReceiptPhoto()));
        } else if (imageUploadTask != null) {
            TransferObserver transferObserver =
                    photoUploadingSpiceManager.getTransferById(imageUploadTask.getAmazonTaskId());
            onStateChanged(transferObserver.getId(), transferObserver.getState());
            transferObserver.setTransferListener(this);
            view.hideScanButton();
            view.attachReceipt(Uri.parse(imageUploadTask.getFilePath()));
        }

        checkVerification();
    }

    public void onAmountChanged(String amount) {
        this.amount = amount;
        checkVerification();
    }

    private void checkVerification() {
        if (!TextUtils.isEmpty(amount) &&
                !TextUtils.isEmpty(dtlTransaction.getReceiptPhoto()))
            view.enableVerification();
        else view.disableVerification();
    }

    public void verify() {
        dtlTransaction.setAmount(Double.parseDouble(amount));
        snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);
        view.openScanQr(dtlPlace, dtlTransaction);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    @State
    UploadTask imageUploadTask;

    public void scanReceipt() {
        eventBus.post(new ImagePickRequestEvent(PickImageDelegate.REQUEST_CAPTURE_PICTURE, REQUESTER_ID));
    }

    public void rescanReceipt() {
        cancelUpload();
        scanReceipt();
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            view.hideScanButton();
            eventBus.removeStickyEvent(event);
            String fileThumbnail = event.getImages()[0].getFileThumbnail();
            if (ValidationUtils.isUrl(fileThumbnail)) {
                imageSelected(Uri.parse(fileThumbnail).toString());
            } else {
                imageSelected(Uri.fromFile(new File(fileThumbnail)).toString());
            }

        }
    }

    private void imageSelected(String filePath) {
        if (view != null) {
            imageUploadTask = new UploadTask();
            imageUploadTask.setFilePath(filePath);
            imageUploadTask.setStatus(UploadTask.Status.IN_PROGRESS);
            savePhotoIfNeeded();
        }
    }

    ////////////////////////////////////////
    /////// Photo upload
    ////////////////////////////////////////

    private void savePhotoIfNeeded() {
        doRequest(new CopyFileCommand(context, imageUploadTask.getFilePath()), this::uploadPhoto);
    }

    private void uploadPhoto(String filePath) {
        imageUploadTask.setFilePath(filePath);
        view.attachReceipt(Uri.parse(filePath));
        startUpload(imageUploadTask);
    }

    private void startUpload(UploadTask uploadTask) {
        view.showProgress();
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(imageUploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        transferObserver.setTransferListener(this);
    }

    @Override
    public void onStateChanged(int id, TransferState state) {
        if (view != null && imageUploadTask != null) {
            if (state.equals(TransferState.COMPLETED)) {
                imageUploadTask.setStatus(UploadTask.Status.COMPLETED);
                imageUploadTask.setOriginUrl(photoUploadingSpiceManager.getResultUrl(imageUploadTask));
                //
                dtlTransaction.setReceiptPhoto(imageUploadTask.getOriginUrl());
                snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);
            } else if (state.equals(TransferState.FAILED)) {
                imageUploadTask.setStatus(UploadTask.Status.FAILED);
            }

            processUploadTask();
            checkVerification();
        }
    }

    @Override
    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
    }

    @Override
    public void onError(int id, Exception ex) {
        imageUploadTask.setStatus(UploadTask.Status.FAILED);
        processUploadTask();
    }

    private void processUploadTask() {
        if (imageUploadTask != null && view != null) {
            switch (imageUploadTask.getStatus()) {
                case IN_PROGRESS:
                    photoInProgress();
                    break;
                case FAILED:
                    photoFailed();
                    break;
                case COMPLETED:
                    photoCompleted();
                    break;
            }
        }
    }

    private void photoInProgress() {
        view.showProgress();
    }

    private void photoCompleted() {
        view.hideProgress();
    }

    private void photoFailed() {
        view.uploadError();
    }

    public void onProgressClicked() {
        if (imageUploadTask.getStatus().equals(UploadTask.Status.FAILED)) {
            startUpload(imageUploadTask);
        }
    }

    private void cancelUpload() {
        if (imageUploadTask != null) {
            photoUploadingSpiceManager.cancelUploading(imageUploadTask);
        }
    }

    public interface View extends Presenter.View {
        void openScanQr(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void hideScanButton();

        void attachReceipt(Uri uri);

        void showProgress();

        void hideProgress();

        void uploadError();

        void enableVerification();

        void disableVerification();
    }
}
