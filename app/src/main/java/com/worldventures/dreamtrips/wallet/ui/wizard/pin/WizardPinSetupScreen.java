package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;

public class WizardPinSetupScreen extends WalletFrameLayout<WizardPinSetupPresenter.Screen, WizardPinSetupPresenter, WizardPinSetupPath>
        implements WizardPinSetupPresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private SweetAlertDialog progressDialog;

    public WizardPinSetupScreen(Context context) {
        super(context);
    }

    public WizardPinSetupScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> onBackClick());
    }

    private void onBackClick() {
        presenter.goToBack();
    }

    @NonNull
    @Override
    public WizardPinSetupPresenter createPresenter() {
        return new WizardPinSetupPresenter(getContext(), getInjector());
    }

    @OnClick(R.id.button_next)
    public void nextClick() {
        presenter.setupPIN();
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

    @Override
    public void notifyError(Throwable throwable) {

    }

    @Override
    public void showProgress() {
        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        progressDialog.dismiss();
    }
}
