package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.EditText;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.pedant.SweetAlert.SweetAlertDialog.PROGRESS_TYPE;
import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;

public class WizardManualInputScreen extends WalletFrameLayout<WizardManualInputPresenter.Screen, WizardManualInputPresenter, WizardManualInputPath>
        implements WizardManualInputPresenter.Screen {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.wallet_wizard_manual_input_scid)
    EditText scidNumberInput;

    private SweetAlertDialog progressDialog;

    public WizardManualInputScreen(Context context) {
        super(context);
    }

    public WizardManualInputScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
    }

    @Override
    public WizardManualInputPresenter createPresenter() {
        return new WizardManualInputPresenter(getContext(), getInjector());
    }

    @OnClick(R.id.wallet_wizard_manual_input_next_btn)
    void onNextClicked() {
        getPresenter().checkBarcode(scidNumberInput.getText().toString());
    }

    @Override
    public void notifyError(Throwable throwable) {

    }

    @Override
    public void showProgress() {
        progressDialog = new SweetAlertDialog(getContext(), PROGRESS_TYPE)
                .setTitleText(getContext().getString(R.string.waller_wizard_scan_barcode_progress_label));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showSuccessWithDelay(Runnable action, long delay) {
        final SweetAlertDialog successDialog = new SweetAlertDialog(getContext(), SUCCESS_TYPE)
                .setTitleText(getContext().getString(R.string.wallet_got_it_label));
        successDialog.setCancelable(false);
        successDialog.show();

        postDelayed(() -> {
            successDialog.dismiss();
            action.run();
        }, delay);
    }
}