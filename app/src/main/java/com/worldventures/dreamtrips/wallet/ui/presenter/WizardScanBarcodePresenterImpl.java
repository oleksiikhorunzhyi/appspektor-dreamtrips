package com.worldventures.dreamtrips.wallet.ui.presenter;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardScanBarcodeScreen;

public class WizardScanBarcodePresenterImpl extends WalletPresenterImpl<WizardScanBarcodeScreen, Parcelable>
        implements WIzardScanBarcodePresenter {
    public WizardScanBarcodePresenterImpl(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void onNewViewState() {

    }

    @Override
    public void applyViewState() {

    }
}
