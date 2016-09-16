package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListScreenPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.BankCardCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackHeaderCell;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.success.WalletSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsScreenPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
      },
      injects = {
            WalletActivityPresenter.class,
            BankCardCell.class,
            CardStackCell.class,
            CardStackHeaderCell.class,
            SettingsRadioCell.class,
            SectionDividerCell.class,
            WizardSplashPresenter.class,
            WizardTermsScreenPresenter.class,
            WizardScanBarcodePresenter.class,
            WizardManualInputPresenter.class,
            WizardEditProfilePresenter.class,
            WalletPinIsSetPresenter.class,
            WizardChargingPresenter.class,
            CardDetailsPresenter.class,
            CardListScreenPresenter.class,
            WalletCardSettingsPresenter.class,
            WalletSuccessPresenter.class,
            AddCardDetailsPresenter.class,
            WizardPinSetupPresenter.class,
            WizardWelcomePresenter.class,
            WalletAutoClearCardsPresenter.class,
            WalletDisableDefaultCardPresenter.class
      },
      complete = false, library = true
)
public class WalletActivityModule {
   public static final String WALLET = "Wallet";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideWalletComponent() {
      return new ComponentDescription(WALLET, R.string.wallet, R.string.wallet, R.drawable.ic_wallet, true, null);
   }
}
