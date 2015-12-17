package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.api.place.EstimatePointsRequest;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.DtlCurrency;
import com.worldventures.dreamtrips.modules.dtl.store.DtlMerchantRepository;

import javax.inject.Inject;

public class DtlPointsEstimationPresenter extends Presenter<DtlPointsEstimationPresenter.View> {

    public static final String BILL_TOTAL = "billTotal";

    protected final String merchantId;

    @Inject
    DtlMerchantRepository dtlMerchantRepository;

    private DtlMerchant dtlMerchant;

    public DtlPointsEstimationPresenter(String merchantId) {
        this.merchantId = merchantId;
    }

    @Override
    public void onInjected() {
        super.onInjected();
        dtlMerchant = dtlMerchantRepository.getMerchantById(merchantId);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
        view.showCurrency(dtlMerchant.getDefaultCurrency());
    }

    public void onCalculateClicked(String userInput) {
        if (!validateInput(userInput)) return;
        //
        view.showProgress();
        doRequest(new EstimatePointsRequest(merchantId,
                dtlMerchant.getDefaultCurrency().getCode(),
                Double.valueOf(userInput)), aDouble -> {
            view.showEstimatedPoints(aDouble.intValue());
        });
    }

    protected boolean validateInput(String pointsInput) {
        if (pointsInput.isEmpty()) {
            view.showError(R.string.dtl_field_validation_empty_input_error);
            return false;
        }
        if (Double.valueOf(pointsInput) < 0D) {
            view.showError(R.string.dtl_points_estimation_negative_input_error);
            return false;
        }
        return true;
    }

    public interface View extends ApiErrorView {

        void showProgress();

        void showError(@StringRes int errorRes);

        void showEstimatedPoints(int value);

        void showCurrency(DtlCurrency dtlCurrency);
    }
}
