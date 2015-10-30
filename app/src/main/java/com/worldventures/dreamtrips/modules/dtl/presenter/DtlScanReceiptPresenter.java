package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
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

public class DtlScanReceiptPresenter extends Presenter<DtlScanReceiptPresenter.View> {

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
        view.openVerify(dtlPlace, dtlTransaction);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    public void scanReceipt() {
        eventBus.post(new ImagePickRequestEvent(PickImageDelegate.REQUEST_CAPTURE_PICTURE, REQUESTER_ID));
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
        savePhotoIfNeeded(filePath);
    }

    private void savePhotoIfNeeded(String filePath) {
        doRequest(new CopyFileCommand(context, filePath), this::attachPhoto);
    }

    private void attachPhoto(String filePath) {
        view.attachReceipt(Uri.parse(filePath));
        dtlTransaction.setReceiptPhoto(filePath);

        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(filePath);
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        dtlTransaction.setUploadTask(uploadTask);

        snapper.saveDtlTransaction(dtlPlace.getId(), dtlTransaction);

        checkVerification();
    }

    public interface View extends Presenter.View {
        void openVerify(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void hideScanButton();

        void attachReceipt(Uri uri);

        void enableVerification();

        void disableVerification();
    }
}
