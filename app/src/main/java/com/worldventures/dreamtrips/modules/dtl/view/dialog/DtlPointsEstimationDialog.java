package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.CalculateDtlPointsClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationDialogPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.dialog_dtl_points_estimation)
public class DtlPointsEstimationDialog extends BaseFragmentWithArgs<DtlPointsEstimationDialogPresenter, PointsEstimationDialogBundle>
        implements DtlPointsEstimationDialogPresenter.View {

    @InjectView(R.id.inputPoints)
    EditText inputPoints;
    @InjectView(R.id.calculateButton)
    Button calculateButton;
    @InjectView(R.id.pointsEstimated)
    TextView pointsEstimated;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected DtlPointsEstimationDialogPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPointsEstimationDialogPresenter(getArgs().getPlaceId());
    }

    @Override
    public void showProgress() {
        // clear possible previous result
        pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
        progressBar.setVisibility(View.VISIBLE);
        calculateButton.setEnabled(false);
    }

    @Override
    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        calculateButton.setEnabled(true);
    }

    @Override
    public void showError(String errorMessage) {
        inputPoints.setError(errorMessage);
    }

    @Override
    public void showEstimatedPoints(String value) {
        pointsEstimated.setText(value);
    }

    @OnClick(R.id.calculateButton)
    void calculateClicked() {
        eventBus.post(new CalculateDtlPointsClickedEvent(inputPoints.getText().toString()));
    }
}
