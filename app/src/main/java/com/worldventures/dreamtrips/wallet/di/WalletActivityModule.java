package com.worldventures.dreamtrips.wallet.di;

import android.app.Activity;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.FlowNavigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.BankCardCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackHeaderCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset.FactoryResetPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.factory_reset_success.FactoryResetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.barcode.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.charging.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.connect_smartcard.ConnectSmartCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.edit_card.EditCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.edit_card.EditCardDetailsScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsScreenPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;

import javax.inject.Singleton;

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
            WizardPowerOnPresenter.class,
            WizardTermsScreenPresenter.class,
            WizardScanBarcodePresenter.class,
            WizardManualInputPresenter.class,
            WizardEditProfilePresenter.class,
            WalletPinIsSetPresenter.class,
            WizardChargingPresenter.class,
            CardDetailsPresenter.class,
            CardListPresenter.class,
            WalletSettingsPresenter.class,
            PinSetSuccessPresenter.class,
            AddCardDetailsPresenter.class,
            WizardPinSetupPresenter.class,
            WizardWelcomePresenter.class,
            EditCardDetailsPresenter.class,
            EditCardDetailsScreen.class,
            WalletAutoClearCardsPresenter.class,
            WalletDisableDefaultCardPresenter.class,
            WalletUpToDateFirmwarePresenter.class,
            WalletSuccessInstallFirmwarePresenter.class,
            WalletFirmwareChecksPresenter.class,
            WalletDownloadFirmwarePresenter.class,
            FactoryResetPresenter.class,
            FactoryResetSuccessPresenter.class,
            ConnectSmartCardPresenter.class,
            WalletInstallFirmwarePresenter.class,
            WalletNewFirmwareAvailablePresenter.class
      },
      complete = false, library = true
)
public class WalletActivityModule {
   public static final String WALLET = "Wallet";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideWalletComponent() {
      return new ComponentDescription(
            WALLET,
            R.string.wallet,
            R.string.wallet,
            R.drawable.ic_wallet,
            true,
            null
      );
   }

   @Singleton
   @Provides
   Navigator provideNavigator(Activity activity) {
      return new FlowNavigator(activity);
   }
}
