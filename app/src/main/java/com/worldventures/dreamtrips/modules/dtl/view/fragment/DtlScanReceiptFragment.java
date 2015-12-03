package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.module.RouteCreatorModule;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.DTEditText;
import com.worldventures.dreamtrips.modules.common.view.dialog.ProgressDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlEnrollWizard;
import com.worldventures.dreamtrips.modules.dtl.model.DTlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.DtlTransaction;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPointsEstimationPresenter;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlScanReceiptPresenter;
import com.worldventures.dreamtrips.modules.dtl.validator.AmountValidator;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import mbanje.kurt.fabbutton.CircleImageView;
import mbanje.kurt.fabbutton.FabButton;

@Layout(R.layout.fragment_scan_receipt)
@MenuResource(R.menu.menu_mock)
public class DtlScanReceiptFragment extends BaseFragmentWithArgs<DtlScanReceiptPresenter, DTlMerchant>
        implements DtlScanReceiptPresenter.View {

    @InjectView(R.id.verify)
    Button verify;
    @InjectView(R.id.scan_receipt)
    Button scanReceipt;
    @InjectView(R.id.receipt)
    SimpleDraweeView receipt;
    @InjectView(R.id.shadow)
    View shadow;
    @InjectView(R.id.fab_progress)
    FabButton fabProgress;
    @InjectView(R.id.fabbutton_circle)
    CircleImageView circleView;
    @InjectView(R.id.amount_input)
    DTEditText amountInput;
    @InjectView(R.id.scan_receipt_note)
    TextView scanReceiptNode;

    @Inject
    @Named(RouteCreatorModule.DTL_TRANSACTION)
    RouteCreator<DtlTransaction> routeCreator;

    protected ProgressDialogFragment progressDialog;
    private DtlEnrollWizard dtlEnrollWizard;

    private TextWatcherAdapter textWatcherAdapter = new TextWatcherAdapter() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getPresenter().onAmountChanged(s.toString());
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setNavigationOnClickListener(v ->
                getActivity().onBackPressed());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        dtlEnrollWizard = new DtlEnrollWizard(router, routeCreator);
        //
        amountInput.addValidator(new AmountValidator(getString(R.string.dtl_amount_invalid)));
        scanReceiptNode.setText(Html.fromHtml(getString(R.string.dtl_receipt_note)));
        progressDialog = ProgressDialogFragment.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        ButterKnife.<Toolbar>findById(getActivity(), R.id.toolbar_actionbar).setTitle(R.string.dtl_enter_amount);
        amountInput.addTextChangedListener(textWatcherAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        amountInput.removeTextChangedListener(textWatcherAdapter);
    }

    @Override
    protected DtlScanReceiptPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlScanReceiptPresenter(getArgs());
    }

    @Override
    public void preSetBillAmount(double amount) {
        amountInput.setText(String.valueOf(amount));
    }

    @OnClick(R.id.verify)
    void onVerify() {
        if (amountInput.validate()) getPresenter().verify();
    }

    @OnClick(R.id.scan_receipt)
    void onImage() {
        getPresenter().scanReceipt();
    }

    @Override
    public void hideScanButton() {
        scanReceipt.setVisibility(View.GONE);
    }

    @Override
    public void attachReceipt(Uri uri) {
        fabProgress.setVisibility(View.VISIBLE);
        receipt.setController(GraphicUtils.provideFrescoResizingController(uri, receipt.getController()));
        fabProgress.showProgress(false);
        fabProgress.setIcon(R.drawable.ic_upload_done, R.drawable.ic_upload_done);
        int color = fabProgress.getContext().getResources().getColor(R.color.bucket_green);
        circleView.setColor(color);
    }

    @Override
    public void enableVerification() {
        verify.setEnabled(true);
    }

    @Override
    public void disableVerification() {
        verify.setEnabled(false);
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        if (errorResponse.containsField(DtlPointsEstimationPresenter.BILL_TOTAL)) {
            amountInput.setError(errorResponse.getMessageForField(DtlPointsEstimationPresenter.BILL_TOTAL));
        }
        return false;
    }

    @Override
    public void onApiCallFailed() {
        progressDialog.dismiss();
    }

    @Override
    public void showProgress() {
        progressDialog.show(getFragmentManager());
    }

    public void openVerify(DTlMerchant DTlMerchant, DtlTransaction dtlTransaction) {
        progressDialog.dismiss();
        dtlEnrollWizard.proceed(getFragmentManager(), dtlTransaction, getArgs());
    }
}
