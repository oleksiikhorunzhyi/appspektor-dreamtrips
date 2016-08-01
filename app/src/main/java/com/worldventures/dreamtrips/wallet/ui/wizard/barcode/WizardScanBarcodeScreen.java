package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.google.zxing.Result;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static cn.pedant.SweetAlert.SweetAlertDialog.SUCCESS_TYPE;

public class WizardScanBarcodeScreen extends WalletFrameLayout<WizardScanBarcodePresenter.Screen, WizardScanBarcodePresenter, WizardScanBarcodePath>
        implements WizardScanBarcodePresenter.Screen, ZXingScannerView.ResultHandler {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.scanner_view)
    ZXingScannerView scanner;

    private SweetAlertDialog progressDialog;

    public WizardScanBarcodeScreen(Context context) {
        super(context);
    }

    public WizardScanBarcodeScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setNavigationOnClickListener(v -> presenter.goBack());
        scanner.setResultHandler(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            getPresenter().requestCamera();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scanner.stopCamera();
    }

    @Override
    public WizardScanBarcodePresenter createPresenter() {
        return new WizardScanBarcodePresenter(getContext(), getInjector());
    }

    @Override
    public void startCamera() {
        scanner.startCamera();
    }

    @Override
    public void showRationaleForCamera() {
        Snackbar.make(this, R.string.permission_camera_rationale, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showDeniedForCamera() {
        Snackbar.make(this, R.string.no_camera_permission, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void handleResult(Result result) {
        getPresenter().barcodeScanned(result.getText());
    }

    @OnClick(R.id.wallet_wizard_scan_barcode_manual_input)
    void onInputManuallyClicked() {
        getPresenter().startManualInput();
    }

    @Override
    public void notifyError(Throwable throwable) {
        //do specific error handling or move it upper
    }

    @Override
    public void showProgress() {
        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE)
                .setContentText(getContext().getString(R.string.waller_wizard_scan_barcode_progress_label));
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