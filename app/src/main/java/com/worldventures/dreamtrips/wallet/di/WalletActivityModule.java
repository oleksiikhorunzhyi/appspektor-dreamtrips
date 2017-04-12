package com.worldventures.dreamtrips.wallet.di;

import android.app.Activity;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.FlowNavigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell.BankCardCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell.CardStackHeaderCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDeviceItemCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceInfoCell;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.address.EditBillingAddressPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.address.EditBillingAddressScreen;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.ConnectionErrorPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.barcode.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WizardAssignUserPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.paymentcomplete.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncRecordsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {
      },
      injects = {
            WalletActivityPresenter.class,
            WalletStartPresenter.class,
            UnsupportedDeviceInfoCell.class,
            SupportedDevicesListCell.class,
            SupportedDeviceItemCell.class,
            BankCardCell.class,
            CardStackCell.class,
            CardStackHeaderCell.class,
            SettingsRadioCell.class,
            SectionDividerCell.class,
            WalletProvisioningBlockedPresenter.class,
            WizardSplashPresenter.class,
            WizardPowerOnPresenter.class,
            WizardTermsPresenter.class,
            WizardScanBarcodePresenter.class,
            WizardManualInputPresenter.class,
            WizardEditProfilePresenter.class,
            WalletPinIsSetPresenter.class,
            WizardChargingPresenter.class,
            CardDetailsPresenter.class,
            CardListPresenter.class,
            WalletSettingsPresenter.class,
            WalletGeneralSettingsPresenter.class,
            WalletSecuritySettingsPresenter.class,
            WalletHelpSettingsPresenter.class,
            WalletSettingsProfilePresenter.class,
            WalletOfflineModeSettingsPresenter.class,
            PinSetSuccessPresenter.class,
            AddCardDetailsPresenter.class,
            WizardPinSetupPresenter.class,
            WizardWelcomePresenter.class,
            EditBillingAddressPresenter.class,
            EditBillingAddressScreen.class,
            WalletAutoClearCardsPresenter.class,
            WalletDisableDefaultCardPresenter.class,
            WalletUpToDateFirmwarePresenter.class,
            WalletSuccessInstallFirmwarePresenter.class,
            WalletFirmwareChecksPresenter.class,
            WalletDownloadFirmwarePresenter.class,
            FactoryResetPresenter.class,
            FactoryResetSuccessPresenter.class,
            WalletInstallFirmwarePresenter.class,
            WizardCheckingPresenter.class,
            WalletNewFirmwareAvailablePresenter.class,
            WalletPuckConnectionPresenter.class,
            WizardAssignUserPresenter.class,
            AboutPresenter.class,
            PairKeyPresenter.class,
            ConnectionErrorPresenter.class,
            StartFirmwareInstallPresenter.class,
            ForceUpdatePowerOnPresenter.class,
            ForcePairKeyPresenter.class,
            LostCardPresenter.class,
            ExistingCardDetectPresenter.class,
            UnassignSuccessPresenter.class,
            EnterPinUnassignPresenter.class,
            SyncRecordsPresenter.class,
            NewCardPowerOnPresenter.class,
            PreCheckNewCardPresenter.class,
            PaymentSyncFinishPresenter.class,
            ExistingDeviceDetectPresenter.class,
            WizardUploadProfilePresenter.class
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
