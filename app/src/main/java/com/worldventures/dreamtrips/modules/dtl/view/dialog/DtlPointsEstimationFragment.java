package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PointsEstimationDialogBundle;
import com.worldventures.dreamtrips.modules.dtl.event.CloseDialogEvent;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_dtl_points_estimation)
public class DtlPointsEstimationFragment extends BaseFragmentWithArgs<DtlPointsEstimationPresenter, PointsEstimationDialogBundle>
        implements DtlPointsEstimationPresenter.View {

    @InjectView(R.id.inputPoints)
    DTEditText inputPoints;
    @InjectView(R.id.calculateButton)
    Button calculateButton;
    @InjectView(R.id.pointsEstimated)
    TextView pointsEstimated;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.info)
    TextView info;

    private TextWatcherAdapter textWatcherAdapter = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //work around for disappearing background of button
            calculateButton.setTextColor(s.length() > 0
                    ? getResources().getColor(R.color.black)
                    : getResources().getColor(R.color.tripButtonDisabled));
        }
    };

    @Override
    protected DtlPointsEstimationPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPointsEstimationPresenter(getArgs().getMerchantId());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
        inputPoints.addTextChangedListener(textWatcherAdapter);
    }

    @Override
    public void onDestroyView() {
        inputPoints.removeTextChangedListener(textWatcherAdapter);
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        // clear possible previous result
        pointsEstimated.setText(R.string.dtl_points_estimation_default_result);
        progressBar.setVisibility(View.VISIBLE);
        calculateButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(@StringRes int errorRes) {
        inputPoints.setError(getString(errorRes));
    }

    @Override
    public void showEstimatedPoints(int value) {
        stopProgress();
        pointsEstimated.setText(getString(R.string.dtl_dt_points, value));
    }

    @OnClick(R.id.infoToggle)
    void infoToggle() {
        info.setVisibility(info.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.calculateButton)
    void calculateClicked() {
        if (inputPoints.getText().length() > 0 && inputPoints.validate())
            getPresenter().onCalculateClicked(inputPoints.getText().toString());
    }

    @OnClick(R.id.button_cancel)
    void onCancel() {
        eventBus.post(new CloseDialogEvent());
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        if (errorResponse.containsField(DtlPointsEstimationPresenter.BILL_TOTAL)) {
            inputPoints.setError(errorResponse.getMessageForField(DtlPointsEstimationPresenter.BILL_TOTAL));
        }
        return false;
    }

    @Override
    public void onApiCallFailed() {
        stopProgress();
    }

    private void stopProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        calculateButton.setVisibility(View.VISIBLE);
    }

}
