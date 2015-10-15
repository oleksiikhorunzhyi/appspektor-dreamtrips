package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransactionResult;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlTransactionSucceedPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.techery.properratingbar.ProperRatingBar;
import io.techery.properratingbar.RatingListener;

@Layout(R.layout.fragment_transaction_succeed)
public class DtlTransactionSucceedFragment extends BaseFragmentWithArgs<DtlTransactionSucceedPresenter, DtlPlace>
        implements DtlTransactionSucceedPresenter.View {

    @InjectView(R.id.congratulations)
    TextView congratulations;

    @InjectView(R.id.rating_bar)
    ProperRatingBar properRatingBar;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        properRatingBar.setListener(stars -> getPresenter().rate(stars));
    }

    @Override
    public void setCongratulations(DtlTransactionResult result) {
        congratulations.setText(result.generateSuccessMessage(getResources()));
    }

    @OnClick(R.id.done)
    void onDoneClicked() {
        getActivity().finish();
    }

    @Override
    protected DtlTransactionSucceedPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlTransactionSucceedPresenter(getArgs());
    }

    private SweetAlertDialog pDialog;

    @Override
    public void hideProgress() {
        if (pDialog != null) pDialog.dismissWithAnimation();
    }

    @Override
    public void showProgress() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.theme_main));
        pDialog.setTitleText(getString(R.string.wait));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void rateSucceed() {
        pDialog.setTitleText("Saved!")
                .setContentText("Your rating was saved!")
                .setConfirmText(getString(R.string.ok))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }
}
