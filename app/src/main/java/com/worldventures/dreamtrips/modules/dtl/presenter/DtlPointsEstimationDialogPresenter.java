package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.widget.Toast;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlPlacePointsEstimationQuery;
import com.worldventures.dreamtrips.modules.dtl.event.CalculateDtlPointsClickedEvent;

public class DtlPointsEstimationDialogPresenter extends Presenter<DtlPointsEstimationDialogPresenter.View> {

    protected int placeId;

    public DtlPointsEstimationDialogPresenter(int placeId) {
        this.placeId = placeId;
    }

    public void onEventMainThread(CalculateDtlPointsClickedEvent event) {
        if (!validateInput(event.getUserInput())) return;
        //
        view.showProgress();
        doRequest(new GetDtlPlacePointsEstimationQuery(placeId,
                Float.valueOf(event.getUserInput())), aFloat -> {
            view.stopProgress();
            view.showEstimatedPoints(String.valueOf(aFloat));
        }, spiceException -> {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            view.stopProgress();
        });
    }

    protected boolean validateInput(String pointsInput) {
        boolean valid = true;
        if (pointsInput.isEmpty()) {
            view.showError("Cannot be empty!");
            valid = false;
        }
        if (valid && Float.valueOf(pointsInput) < 0F) {
            view.showError("Cannot be less than 0!");
            valid = false;
        }
        return valid;
    }

    public interface View extends Presenter.View {

        void showProgress();

        void stopProgress();

        void showError(String errorMessage);

        void showEstimatedPoints(String value);
    }
}
