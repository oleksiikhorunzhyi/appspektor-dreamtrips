package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
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
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlScanReceiptPresenter extends JobPresenter<DtlScanReceiptPresenter.View> {

    public static final int REQUESTER_ID = -3;

    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlTransactionService transactionService;
    //
    @State
    String amount;
    //
    private final String merchantId;
    private DtlMerchant dtlMerchant;

    public DtlScanReceiptPresenter(String merchantId) {
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
        apiErrorPresenter.setView(view);
        transactionService.transactionActionPipe().createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> {
                    if (transaction.getUploadTask() != null) {
                        view.hideScanButton();
                        view.attachReceipt(Uri.parse(transaction.getUploadTask().getFilePath()));
                    }
                    //
                    if (transaction.getBillTotal() != 0d) {
                        view.preSetBillAmount(transaction.getBillTotal());
                        this.amount = String.valueOf(transaction.getBillTotal());
                    }
                }, apiErrorPresenter::handleError);
        //

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
        if (!TextUtils.isEmpty(amount))
            transactionService.transactionActionPipe().createObservableResult(DtlTransactionAction.get(dtlMerchant))
                    .map(DtlTransactionAction::getResult)
                    .filter(transaction -> transaction.getUploadTask() != null)
                    .compose(bindViewIoToMainComposer())
                    .subscribe(transaction -> view.enableVerification(), apiErrorPresenter::handleError);
        else view.disableVerification();
    }

    private void bindApiJob() {
        transactionService.estimatePointsActionPipe().observeWithReplay()
                .takeUntil(state -> state.status == ActionState.Status.SUCCESS
                        || state.status == ActionState.Status.FAIL)
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlEstimatePointsAction>()
                        .onStart(action -> view.showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> attachDtPoints(action.getEstimationPointsHolder().getPoints())));
    }

    public void verify() {
        TrackingHelper.dtlVerifyAmountUser(amount);
        transactionService.transactionActionPipe()
                .createObservableResult(
                        DtlTransactionAction.update(dtlMerchant,
                                transaction -> ImmutableDtlTransaction.copyOf(transaction)
                                        .withBillTotal(Double.parseDouble(amount)))
                )
                .map(DtlTransactionAction::getResult)
                .flatMap(transaction -> transactionService.estimatePointsActionPipe().createObservableResult(
                        new DtlEstimatePointsAction(dtlMerchant, transaction.getBillTotal(), dtlMerchant.getDefaultCurrency().getCode()))
                ).subscribe(action -> {
        }, apiErrorPresenter::handleError);
    }

    private void attachDtPoints(Double points) {
        TrackingHelper.dtlVerifyAmountSuccess();
        transactionService.transactionActionPipe()
                .createObservable(
                        DtlTransactionAction.update(dtlMerchant,
                                transaction -> ImmutableDtlTransaction.copyOf(transaction)
                                        .withPoints(points))
                ).compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> view.openVerify(action.getResult())));
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
        transactionService.transactionActionPipe()
                .createObservable(DtlTransactionAction.update(dtlMerchant,
                        transaction -> ImmutableDtlTransaction.copyOf(transaction)
                                .withUploadTask(uploadTask)))
                .subscribe(new ActionStateSubscriber<DtlTransactionAction>()
                        .onFail(apiErrorPresenter::handleActionError));
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
