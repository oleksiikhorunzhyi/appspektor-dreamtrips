package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.api.place.GetDtlPlacePointsEstimationQuery;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

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
    private ApiErrorPresenter apiErrorPresenter;

    public DtlScanReceiptPresenter(DtlPlace place) {
        this.dtlPlace = place;
        apiErrorPresenter = new ApiErrorPresenter();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        dtlTransaction = snapper.getDtlTransaction(dtlPlace.getMerchantId());

        if (dtlTransaction.getUploadTask() != null) {
            view.hideScanButton();
            view.attachReceipt(Uri.parse(dtlTransaction.getUploadTask().getFilePath()));
        }

        checkVerification();
    }

    public void onAmountChanged(String amount) {
        this.amount = amount;
        checkVerification();
    }

    private void checkVerification() {
        if (!TextUtils.isEmpty(amount) &&
                dtlTransaction.getUploadTask() != null)
            view.enableVerification();
        else view.disableVerification();
    }

    public void verify() {
        view.showProgress();
        //
        dtlTransaction.setAmount(Double.parseDouble(amount));
        doRequest(new GetDtlPlacePointsEstimationQuery(dtlPlace.getMerchantId(), dtlTransaction.getAmount()),
                this::attachDtPoints, apiErrorPresenter::handleError);
    }

    private void attachDtPoints(Double points) {
        dtlTransaction.setAmount(Double.parseDouble(amount));
        dtlTransaction.setPoints(points);
        //
        snapper.saveDtlTransaction(dtlPlace.getMerchantId(), dtlTransaction);
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
            imageSelected(Uri.parse(fileThumbnail).toString());
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

        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(filePath);
        TransferObserver transferObserver = photoUploadingSpiceManager.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));

        dtlTransaction.setUploadTask(uploadTask);

        snapper.saveDtlTransaction(dtlPlace.getMerchantId(), dtlTransaction);

        checkVerification();
    }

    public interface View extends ApiErrorView {
        void openVerify(DtlPlace dtlPlace, DtlTransaction dtlTransaction);

        void hideScanButton();

        void attachReceipt(Uri uri);

        void enableVerification();

        void disableVerification();

        void showProgress();
    }
}
