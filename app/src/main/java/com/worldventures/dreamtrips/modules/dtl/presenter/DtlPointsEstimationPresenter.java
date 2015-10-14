package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlPlacePointsEstimationQuery;

public class DtlPointsEstimationPresenter extends Presenter<DtlPointsEstimationPresenter.View> {

    protected int placeId;

    public DtlPointsEstimationPresenter(int placeId) {
        this.placeId = placeId;
    }

    public void onCalculateClicked(String userInput) {
        if (!validateInput(userInput)) return;
        //
        view.showProgress();
        doRequest(new GetDtlPlacePointsEstimationQuery(placeId,
                Double.valueOf(userInput)), aDouble -> {
            view.stopProgress();
            view.showEstimatedPoints(String.valueOf(aDouble));
        }, spiceException -> {
            super.handleError(spiceException);
            view.stopProgress();
        });
    }

    protected boolean validateInput(String pointsInput) {
        if (pointsInput.isEmpty()) {
            view.showError(R.string.dtl_points_estimation_empty_input_error);
            return false;
        }
        if (Double.valueOf(pointsInput) < 0D) {
            view.showError(R.string.dtl_points_estimation_negative_input_error);
            return false;
        }
        return true;
    }

    public interface View extends Presenter.View {

        void showProgress();

        void stopProgress();

        void showError(@StringRes int errorRes);

        void showEstimatedPoints(String value);
    }
}
