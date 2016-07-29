package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import flow.Flow;
import flow.History;

public class WizardCardNamePresenter extends WalletPresenter<WizardCardNamePresenter.Screen, Parcelable> {
    public WizardCardNamePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext())
                .setHistory(History.single(new WizardSplashPath()), Flow.Direction.BACKWARD);
    }

    public void goToNext() {
        Flow.get(getContext()).set(new WizardEditProfilePath());
    }

    public interface Screen extends WalletScreen {

    }
}
