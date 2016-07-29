package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePath;

import flow.Flow;

public class WizardSplashScreenPresenter extends WalletPresenter<WizardSplashScreenPresenter.Screen, Parcelable> {
    public WizardSplashScreenPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void startScanCard() {
        Flow.get(getContext()).set(new WizardScanBarcodePath());
    }

    public interface Screen extends WalletScreen {
    }
}
