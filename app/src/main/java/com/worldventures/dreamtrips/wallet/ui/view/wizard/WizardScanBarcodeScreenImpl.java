package com.worldventures.dreamtrips.wallet.ui.view.wizard;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.presenter.WIzardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.presenter.WizardScanBarcodePresenterImpl;

public class WizardScanBarcodeScreenImpl extends WalletFrameLayout<WizardScanBarcodeScreen, WIzardScanBarcodePresenter, WizardScanBarcodePath>
        implements WizardScanBarcodeScreen {


    public WizardScanBarcodeScreenImpl(Context context) {
        super(context);
    }

    public WizardScanBarcodeScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public WIzardScanBarcodePresenter createPresenter() {
        return new WizardScanBarcodePresenterImpl(getContext(), getInjector());
    }
}
