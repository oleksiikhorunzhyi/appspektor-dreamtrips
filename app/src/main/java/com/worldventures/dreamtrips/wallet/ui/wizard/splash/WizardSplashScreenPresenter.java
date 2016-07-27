package com.worldventures.dreamtrips.wallet.ui.wizard.splash;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePath;

import flow.Flow;
import flow.History;

public class WizardSplashScreenPresenter extends WalletPresenter<WizardSplashScreenPresenter.WizardSplashScreen, Parcelable>
        implements ViewStateMvpPresenter<WizardSplashScreenPresenter.WizardSplashScreen, Parcelable> {
    public WizardSplashScreenPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void startScanCard() {
        Flow flow = Flow.get(getContext());
        History.Builder historyBuilder = flow.getHistory()
                .buildUpon()
                .push(new WizardScanBarcodePath());

        flow.setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
    }

    public interface WizardSplashScreen extends WalletScreen {
    }
}
