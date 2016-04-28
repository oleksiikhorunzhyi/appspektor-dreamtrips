package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.CopyFileCommand;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.ImmutableDtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.store.DtlJobManager;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantStore;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import javax.inject.Inject;

import icepick.State;

public class DtlScanReceiptPresenter extends JobPresenter<DtlScanReceiptPresenter.View> {

    public static final int REQUESTER_ID = -3;

    @Inject
    SnappyRepository db;
    @Inject
    DtlMerchantStore merchantStore;
    @Inject
    DtlJobManager jobManager;
    //
    @State
    String amount;
    //
    private final String merchantId;
    private DtlMerchant dtlMerchant;
    private DtlTransaction dtlTransaction;

    public DtlScanReceiptPresenter(String merchantId) {
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
        dtlTransaction = db.getDtlTransaction(merchantId);
        //
        if (dtlTransaction.getUploadTask() != null) {
            view.hideScanButton();
            view.attachReceipt(Uri.parse(dtlTransaction.getUploadTask().getFilePath()));
        }
        //
        if (dtlTransaction.getBillTotal() != 0d) {
            view.preSetBillAmount(dtlTransaction.getBillTotal());
            this.amount = String.valueOf(dtlTransaction.getBillTotal());
        }
        //
        checkVerification();
        //
        view.showCurrency(dtlMerchant.getDefaultCurrency());
        //
        bindApiJob();
    }

    public void onAmountChanged(String amount) {
        this.amount = amount;
        checkVerification();
    }

    private void checkVerification() {
        if (!TextUtils.isEmpty(amount) && dtlTransaction.getUploadTask() != null)
            view.enableVerification();
        else view.disableVerification();
    }

    private void bindApiJob() {
        bindJobCached(jobManager.estimatePointsExecutor)
                .onProgress(view::showProgress)
                .onSuccess(dataHolder -> attachDtPoints(dataHolder.getPoints()))
                .onError(apiErrorPresenter::handleError);
    }

    public void verify() {
        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                .withBillTotal(Double.parseDouble(amount));
        TrackingHelper.dtlVerifyAmountUser(amount);
        //
        jobManager.estimatePointsExecutor.createJobWith(merchantId, dtlTransaction.getBillTotal(),
                dtlMerchant.getDefaultCurrency().getCode()).subscribe();
    }

    private void attachDtPoints(Double points) {
        TrackingHelper.dtlVerifyAmountSuccess();
        //
        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                .withPoints(points);
        //
        db.saveDtlTransaction(merchantId, dtlTransaction);
        view.openVerify(dtlTransaction);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Photo picking
    ///////////////////////////////////////////////////////////////////////////

    public void scanReceipt() {
        eventBus.post(new ImagePickRequestEvent(PickImageDelegate.CAPTURE_PICTURE, REQUESTER_ID));
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
        TrackingHelper.dtlCaptureReceipt(filePath);
        view.attachReceipt(Uri.parse(filePath));
        //
        UploadTask uploadTask = new UploadTask();
        uploadTask.setFilePath(filePath);
        TransferObserver transferObserver = photoUploadingManagerS3.upload(uploadTask);
        uploadTask.setAmazonTaskId(String.valueOf(transferObserver.getId()));
        //
        dtlTransaction = ImmutableDtlTransaction.copyOf(dtlTransaction)
                .withUploadTask(uploadTask);
        db.saveDtlTransaction(merchantId, dtlTransaction);
        //
        checkVerification();
    }

    public interface View extends RxView, ApiErrorView {
        void openVerify(DtlTransaction dtlTransaction);

        void hideScanButton();

        void attachReceipt(Uri uri);

        void enableVerification();

        void disableVerification();

        void showProgress();

        void preSetBillAmount(double amount);

        void showCurrency(DtlCurrency dtlCurrency);
    }
}
