package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.api.dtl.merchats.RatingHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchats.requrest.ImmutableRatingParams;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.modules.common.model.ShareType;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.ShareEventProvider;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionRatingEvent;
import com.worldventures.dreamtrips.modules.dtl.analytics.TransactionSuccessEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.transaction.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlTransactionAction;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class DtlTransactionSucceedPresenter
        extends JobPresenter<DtlTransactionSucceedPresenter.View> {

    @Inject
    DtlMerchantInteractor merchantInteractor;
    @Inject
    DtlTransactionInteractor transactionInteractor;
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
        merchantInteractor.merchantByIdPipe()
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
        transactionInteractor.transactionActionPipe()
                .createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> view.showShareDialog(
                        (int) transaction.getDtlTransactionResult().getEarnedPoints(), dtlMerchant),
                        apiErrorPresenter::handleError);
    }

    public void done() {
        if (stars == 0) return;
        transactionInteractor.transactionActionPipe()
                .createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .flatMap(transaction ->
                        transactionInteractor.rateActionPipe()
                                .createObservableResult(
                                        new RatingHttpAction(dtlMerchant.getId(),
                                                ImmutableRatingParams.builder()
                                                        .rating(stars)
                                                        .transactionId(transaction
                                                                .getDtlTransactionResult().getId())
                                                        .build())))
                .compose(bindViewIoToMainComposer())
                .subscribe(action -> {
                }, apiErrorPresenter::handleError);
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(new TransactionRatingEvent(dtlMerchant, stars)));
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        transactionInteractor.transactionActionPipe()
                .createObservableResult(DtlTransactionAction.get(dtlMerchant))
                .map(DtlTransactionAction::getResult)
                .compose(bindViewIoToMainComposer())
                .subscribe(transaction -> {
                            view.setCongratulations(transaction.getDtlTransactionResult());
                            analyticsInteractor.dtlAnalyticsCommandPipe()
                                    .send(DtlAnalyticsCommand.create(
                                            new TransactionSuccessEvent(dtlMerchant, transaction)));
                        },
                        apiErrorPresenter::handleError);
        bindApiPipe();
    }

    private void bindApiPipe() {
        transactionInteractor.rateActionPipe().observe()
                .subscribe(new ActionStateSubscriber<RatingHttpAction>()
                        .onFail(apiErrorPresenter::handleActionError));
    }

    public void trackSharing(@ShareType String type) {
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(
                        ShareEventProvider.provideTransactionSuccessShareEvent(dtlMerchant, type)));
    }

    public interface View extends ApiErrorView, RxView {
        void showShareDialog(int amount, DtlMerchant DtlMerchant);

        void setCongratulations(DtlTransactionResult result);
    }
}
