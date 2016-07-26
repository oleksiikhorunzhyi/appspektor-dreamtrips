package com.worldventures.dreamtrips.wallet.ui.view.wizard;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.presenter.WIzardScanBarcodePresenter;

public class WizardScanBarcodeScreenImpl extends WalletLinearLayout<WizardScanBarcodeScreen, WIzardScanBarcodePresenter, WizardScanBarcodePath>
        implements WizardScanBarcodeScreen {


    public WizardScanBarcodeScreenImpl(Context context) {
        super(context);
    }

    public WizardScanBarcodeScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public WIzardScanBarcodePresenter createPresenter() {
        return null;
    }
}
