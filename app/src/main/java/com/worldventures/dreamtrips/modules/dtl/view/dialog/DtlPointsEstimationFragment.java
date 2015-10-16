package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_points_estimation)
public class DtlPointsEstimationFragment extends BaseFragmentWithArgs<DtlPointsEstimationPresenter, PointsEstimationDialogBundle>
        implements DtlPointsEstimationPresenter.View {

    @InjectView(R.id.inputPoints)
    EditText inputPoints;
    @InjectView(R.id.calculateButton)
    Button calculateButton;
    @InjectView(R.id.pointsEstimated)
    TextView pointsEstimated;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected DtlPointsEstimationPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPointsEstimationPresenter(getArgs().getPlaceId());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
    }

    @Override
    public void showProgress() {
        // clear possible previous result
        pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
        progressBar.setVisibility(View.VISIBLE);
        calculateButton.setVisibility(View.INVISIBLE);
        calculateButton.setEnabled(false);
    }

    @Override
    public void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        calculateButton.setVisibility(View.VISIBLE);
        calculateButton.setEnabled(true);
    }

    @Override
    public void showError(@StringRes int errorRes) {
        inputPoints.setError(getString(errorRes));
    }

    @Override
    public void showEstimatedPoints(int value) {
        pointsEstimated.setText(getString(R.string.dtl_dt_points, value));
    }

    @OnClick(R.id.calculateButton)
    void calculateClicked() {
        getPresenter().onCalculateClicked(inputPoints.getText().toString());
    }

    @OnClick(R.id.button_cancel)
    void onCancel() {
        eventBus.post(new CloseDialogEvent());
    }
}
