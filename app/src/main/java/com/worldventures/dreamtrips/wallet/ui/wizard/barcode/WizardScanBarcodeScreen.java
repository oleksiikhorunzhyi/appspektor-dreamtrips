package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;

import com.google.zxing.Result;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;

import butterknife.InjectView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class WizardScanBarcodeScreen extends WalletFrameLayout<WizardScanBarcodePresenter.WizardScanBarcodeScreen, WizardScanBarcodePresenter, WizardScanBarcodePath>
        implements WizardScanBarcodePresenter.WizardScanBarcodeScreen, ZXingScannerView.ResultHandler {
    @InjectView(R.id.scanner_view)
    ZXingScannerView scanner;

    public WizardScanBarcodeScreen(Context context) {
        super(context);
    }

    public WizardScanBarcodeScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        scanner.setResultHandler(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getPresenter().requestCamera();
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
}
