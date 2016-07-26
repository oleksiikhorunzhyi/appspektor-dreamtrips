package com.worldventures.dreamtrips.wallet.ui.presenter;

import android.os.Parcelable;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.worldventures.dreamtrips.wallet.ui.view.wizard.WizardSplashScreen;

public interface WizardSplashScreenPresenter extends ViewStateMvpPresenter<WizardSplashScreen, Parcelable> {
    void startScanCard();
}
