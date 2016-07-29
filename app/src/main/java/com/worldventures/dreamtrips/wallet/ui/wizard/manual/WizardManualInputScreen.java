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
        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE)
                .setContentText("Test progress");
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }
}