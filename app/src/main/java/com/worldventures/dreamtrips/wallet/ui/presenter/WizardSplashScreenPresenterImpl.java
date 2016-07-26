package com.worldventures.dreamtrips.wallet.ui.presenter;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardScanBarcodePath;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardSplashScreen;

import flow.Flow;
import flow.History;

public class WizardSplashScreenPresenterImpl extends WalletPresenterImpl<WizardSplashScreen, Parcelable>
        implements WizardSplashScreenPresenter {
    public WizardSplashScreenPresenterImpl(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void onNewViewState() {
    }

    @Override
    public void applyViewState() {
    }

    @Override
    public void startScanCard() {
        Flow flow = Flow.get(getContext());
        History.Builder historyBuilder = flow.getHistory()
                .buildUpon()
                .push(new WizardScanBarcodePath());

        flow.setHistory(historyBuilder.build(), Flow.Direction.FORWARD);
    }
}
