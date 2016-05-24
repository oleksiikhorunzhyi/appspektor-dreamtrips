package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlRateAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantService;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionService;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionSucceedPresenter extends JobPresenter<DtlTransactionSucceedPresenter.View> {

    @Inject
    DtlMerchantService merchantService;
    @Inject
    DtlTransactionService transactionService;
    //
    @State
    int stars;
    //
    private final String merchantId;
    private DtlMerchant dtlMerchant;

    public DtlTransactionSucceedPresenter(String merchantId) {
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

    public void rate(int stars) {
        this.stars = stars;
    }

    public void share() {
        transactionService.transactionActionPipe().createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction ->
                                view.showShareDialog((int) transaction.getDtlTransactionResult().getEarnedPoints(), dtlMerchant),
                        apiErrorPresenter::handleError);
    }

    public void done() {
        if (stars != 0) {
            transactionService.transactionActionPipe().createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                    .map(DtlTransactionAction::getResult)
                    .flatMap(transaction ->
                            transactionService.rateActionPipe().createObservableSuccess(new DtlRateAction(merchantId, stars, transaction.getDtlTransactionResult().getId()))
                    ).compose(bindViewIoToMainComposer())
                    .subscribe(action -> {
                    }, apiErrorPresenter::handleError);

        }
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        transactionService.transactionActionPipe().createObservableSuccess(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> view.setCongratulations(transaction.getDtlTransactionResult()),
                        apiErrorPresenter::handleError);
        bindApiPipe();
    }

    private void bindApiPipe() {
        transactionService.rateActionPipe().observe()
                .subscribe(new ActionStateSubscriber<DtlRateAction>()
                        .onFail(apiErrorPresenter::handleActionError));
    }

    /**
     * Analytic-related
     */
    public void trackSharing(@ShareType String type) {
        TrackingHelper.dtlShare(type);
    }

    public interface View extends ApiErrorView, RxView {
        void showShareDialog(int amount, DtlMerchant DtlMerchant);

        void setCongratulations(DtlTransactionResult result);
    }
}
