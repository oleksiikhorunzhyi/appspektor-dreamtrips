package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.api.place.GetDtlPlacePointsEstimationQuery;

public class DtlPointsEstimationPresenter extends Presenter<DtlPointsEstimationPresenter.View> {

    public static final String BILL_TOTAL = "billTotal";

    protected String placeId;

    private ApiErrorPresenter apiErrorPresenter;

    public DtlPointsEstimationPresenter(String placeId) {
        this.placeId = placeId;
        apiErrorPresenter = new ApiErrorPresenter();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        apiErrorPresenter.setView(view);
    }

    public void onCalculateClicked(String userInput) {
        if (!validateInput(userInput)) return;
        //
        view.showProgress();
        doRequest(new GetDtlPlacePointsEstimationQuery(placeId,
                Double.valueOf(userInput)), aDouble -> {
            view.showEstimatedPoints(aDouble.intValue());
        }, apiErrorPresenter::handleError);
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
    }
}
