package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_alias.WizardCardNamePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashScreenPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
        },
        injects = {
                WalletActivityPresenter.class,
                WizardSplashScreenPresenter.class,
                WizardScanBarcodePresenter.class,
                WizardCardNamePresenter.class,
                WizardEditProfilePresenter.class
        },
        complete = false, library = true
)
public class WalletActivityModule {
    public static final String WALLET = "Wallet";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(WALLET, R.string.wallet, R.string.wallet, R.drawable.ic_messenger,
                true, null);
    }
}
