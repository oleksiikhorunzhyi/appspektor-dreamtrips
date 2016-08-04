package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.pedant.SweetAlert.SweetAlertDialog.PROGRESS_TYPE;
import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;

public class WizardManualInputScreen extends WalletFrameLayout<WizardManualInputPresenter.Screen, WizardManualInputPresenter, WizardManualInputPath>
        implements WizardManualInputPresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.wallet_wizard_manual_input_scid)
    EditText scidNumberInput;

    private SweetAlertDialog sweetAlertDialog;

    public WizardManualInputScreen(Context context) {
        super(context);
    }

    public WizardManualInputScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> getPresenter().goBack());
    }

    @NonNull @Override public WizardManualInputPresenter createPresenter() {
        return new WizardManualInputPresenter(getContext(), getInjector());
    }

    @OnEditorAction(R.id.wallet_wizard_manual_input_scid) boolean actionNext(int action) {
        if (action == EditorInfo.IME_ACTION_NEXT) {
            getPresenter().checkBarcode(scidNumberInput.getText().toString());
            return true;
        }
        return false;
    }

    @OnClick(R.id.wallet_wizard_manual_input_next_btn) void onNextClicked() {
        getPresenter().checkBarcode(scidNumberInput.getText().toString());
    }

    @Override public void notifyError(Throwable throwable) {
        if (sweetAlertDialog != null) sweetAlertDialog.dismiss();
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(getString(R.string.wallet_wizard_scid_validation_error));
        sweetAlertDialog.show();
    }

    @Override public void showProgress() {
        if (sweetAlertDialog != null) sweetAlertDialog.dismiss();
        sweetAlertDialog = new SweetAlertDialog(getContext(), PROGRESS_TYPE);
        sweetAlertDialog.setTitleText(getContext().getString(R.string.waller_wizard_scan_barcode_progress_label));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
    }

    @Override public void hideProgress() {
        sweetAlertDialog.dismiss();
    }

    @Override public void showSuccessWithDelay(Runnable action, long delay) {
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