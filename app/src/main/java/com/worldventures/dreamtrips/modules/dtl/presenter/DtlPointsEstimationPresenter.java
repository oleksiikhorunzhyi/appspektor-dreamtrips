package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.ImmediateComposer;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlEstimatePointsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class DtlPointsEstimationPresenter extends JobPresenter<DtlPointsEstimationPresenter.View> {

    public static final String BILL_TOTAL = "billTotal";
    private static final String NUMBER_REGEX = "[+-]?\\d*(\\.\\d+)?";

    protected final String merchantId;

    @Inject
    DtlTransactionInteractor transactionInteractor;
    @Inject
    DtlMerchantInteractor merchantInteractor;
    //
    private DtlMerchant dtlMerchant;

    public DtlPointsEstimationPresenter(String merchantId) {
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

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        view.showCurrency(dtlMerchant.getDefaultCurrency());
        bindApiJob();
    }

    private void bindApiJob() {
        transactionInteractor.estimatePointsActionPipe().observeWithReplay()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<DtlEstimatePointsAction>()
                        .onStart(action -> view.showProgress())
                        .onFail(apiErrorPresenter::handleActionError)
                        .onSuccess(action -> view.showEstimatedPoints(action.getEstimationPointsHolder().getPointsInteger())));
    }

    public void onCalculateClicked(String userInput) {
        if (!validateInput(userInput)) return;
        //
        transactionInteractor.estimatePointsActionPipe()
                .send(new DtlEstimatePointsAction(dtlMerchant, Double.valueOf(userInput), dtlMerchant.getDefaultCurrency().getCode()));
    }

    protected boolean validateInput(String pointsInput) {
        if (pointsInput.isEmpty() || !pointsInput.matches(NUMBER_REGEX)) {
            view.showError(R.string.dtl_field_validation_empty_input_error);
            return false;
        }
        if (Double.valueOf(pointsInput) < 0D) {
            view.showError(R.string.dtl_points_estimation_negative_input_error);
            return false;
        }
        return true;
    }

    public interface View extends RxView, ApiErrorView {

        void showProgress();

        void showError(@StringRes int errorRes);

        void showEstimatedPoints(int value);

        void showCurrency(DtlCurrency dtlCurrency);
    }
}
