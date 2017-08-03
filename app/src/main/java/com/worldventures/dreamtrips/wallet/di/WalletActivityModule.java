package com.worldventures.dreamtrips.wallet.di;

import android.app.Activity;

import com.bluelinelabs.conductor.Router;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.picker.MediaPickerModule;
import com.worldventures.dreamtrips.modules.video.service.MemberVideosInteractor;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAccessValidator;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageServiceImpl;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.location.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;
import com.worldventures.dreamtrips.wallet.ui.common.LocationScreenComponent;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.CoreNavigatorImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorImpl;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.impl.CardListPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.dashboard.impl.CardListScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDeviceItemCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.SupportedDevicesListCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.cell.UnsupportedDeviceInfoCell;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.add.impl.AddCardDetailsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.records.add.impl.AddCardDetailsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.ConnectionErrorPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.impl.ConnectionErrorPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.records.connectionerror.impl.ConnectionErrorScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.detail.impl.CardDetailsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.records.detail.impl.CardDetailsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.impl.WizardChargingPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.impl.WizardChargingScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SectionDividerCell;
import com.worldventures.dreamtrips.wallet.ui.settings.common.cell.SettingsRadioCell;
import com.worldventures.dreamtrips.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.impl.AboutPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.impl.AboutScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwarePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwarePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwarePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailablePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailableScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwarePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.impl.WalletGeneralSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.impl.WalletGeneralSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl.WalletSettingsProfilePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl.WalletSettingsProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.impl.FactoryResetPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.impl.FactoryResetScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.SendFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.impl.SendFeedbackPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.impl.SendFeedbackScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.impl.WalletHelpSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.impl.WalletHelpSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.WalletHelpVideoPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl.WalletHelpVideoPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl.WalletHelpVideoScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.impl.WalletSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.impl.WalletSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.WalletDisableDefaultCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl.WalletDisableDefaultCardPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl.WalletDisableDefaultCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.impl.WalletSecuritySettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.impl.WalletSecuritySettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl.LostCardPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl.LostCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl.WalletAutoClearCardsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl.WalletAutoClearCardsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.impl.WalletStartPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.start.impl.WalletStartScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.WizardAssignUserPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.impl.WizardAssignUserPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.impl.WizardAssignUserScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.impl.WizardCheckingPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.impl.WizardCheckingScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl.WizardManualInputPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl.WizardManualInputScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodeScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl.PairKeyPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl.PairKeyScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.impl.EnterPinPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.impl.EnterPinScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.impl.PinProposalPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.impl.PinProposalScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl.PinSetSuccessPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl.PinSetSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.impl.WizardPowerOnPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.impl.WizardPowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.impl.WizardEditProfilePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.impl.WizardEditProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.impl.WizardUploadProfilePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.impl.WizardUploadProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.impl.SyncRecordsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.impl.SyncRecordsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.impl.WizardSplashPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.impl.WizardSplashScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.impl.WizardTermsPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.impl.WizardTermsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.impl.WizardWelcomePresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.impl.WizardWelcomeScreenImpl;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@Module(
      includes = {
            MediaPickerModule.class
      },
      injects = {
            WalletActivityPresenter.class,
            WalletStartScreenImpl.class,
            UnsupportedDeviceInfoCell.class,
            SupportedDevicesListCell.class,
            SupportedDeviceItemCell.class,
            SettingsRadioCell.class,
            SectionDividerCell.class,
            WalletProvisioningBlockedScreenImpl.class,
            WizardSplashScreenImpl.class,
            WizardPowerOnScreenImpl.class,
            WizardTermsScreenImpl.class,
            WizardScanBarcodeScreenImpl.class,
            WizardManualInputScreenImpl.class,
            WizardEditProfileScreenImpl.class,
            WalletPinIsSetScreenImpl.class,
            WizardChargingScreenImpl.class,
            CardDetailsScreenImpl.class,
            CardListScreenImpl.class,
            WalletSettingsScreenImpl.class,
            WalletGeneralSettingsScreenImpl.class,
            WalletSecuritySettingsScreenImpl.class,
            WalletHelpSettingsScreenImpl.class,
            WalletSettingsProfileScreenImpl.class,
            WalletOfflineModeSettingsScreenImpl.class,
            WalletCustomerSupportSettingsScreenImpl.class,
            DisplayOptionsSettingsScreenImpl.class,
            PinSetSuccessScreenImpl.class,
            AddCardDetailsScreenImpl.class,
            EnterPinScreenImpl.class,
            WizardWelcomeScreenImpl.class,
            WalletAutoClearCardsScreenImpl.class,
            WalletDisableDefaultCardScreenImpl.class,
            WalletUpToDateFirmwareScreenImpl.class,
            WalletSuccessInstallFirmwareScreenImpl.class,
            WalletFirmwareChecksScreenImpl.class,
            WalletDownloadFirmwareScreenImpl.class,
            FactoryResetScreenImpl.class,
            FactoryResetSuccessScreenImpl.class,
            WalletInstallFirmwareScreenImpl.class,
            WizardCheckingScreenImpl.class,
            WalletNewFirmwareAvailableScreenImpl.class,
            WalletPuckConnectionScreenImpl.class,
            WizardAssignUserScreenImpl.class,
            AboutScreenImpl.class,
            PairKeyScreenImpl.class,
            ConnectionErrorScreenImpl.class,
            StartFirmwareInstallScreenImpl.class,
            ForceUpdatePowerOnScreenImpl.class,
            ForcePairKeyScreenImpl.class,
            LostCardScreenImpl.class,
            ExistingCardDetectScreenImpl.class,
            UnassignSuccessScreenImpl.class,
            EnterPinUnassignScreenImpl.class,
            SyncRecordsScreenImpl.class,
            NewCardPowerOnScreenImpl.class,
            PreCheckNewCardScreenImpl.class,
            PaymentSyncFinishScreenImpl.class,
            ExistingDeviceDetectScreenImpl.class,
            WizardUploadProfileScreenImpl.class,
            WalletHelpDocumentsScreenImpl.class,
            SendFeedbackScreenImpl.class,
            WalletHelpVideoScreenImpl.class,
            HelpDocumentDetailScreenImpl.class,
            PinProposalScreenImpl.class,
            PaymentFeedbackScreenImpl.class
      },
      complete = false, library = true
)
public class WalletActivityModule {
   public static final String WALLET = "Wallet";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideWalletComponent() {
      return new ComponentDescription.Builder()
            .key(WALLET)
            .navMenuTitle(R.string.wallet)
            .toolbarTitle(R.string.wallet)
            .icon(R.drawable.ic_wallet)
            .skipGeneralToolbar(true)
            .shouldFinishMainActivity(true)
            .build();
   }

   @Singleton
   @Provides
   Router provideRouter(Activity activity) {
      return ((WalletActivity) activity).getRouter();
   }

   @Singleton
   @Provides
   Navigator provideConductorNavigator(Lazy<Router> router, com.worldventures.dreamtrips.core.navigation.router.Router coreRouter) {
      return new NavigatorImpl(router, new CoreNavigatorImpl(coreRouter));
   }

   @Provides
   @Singleton
   WalletCropImageService provideWalletCropImageDelegate(Activity activity) {
      return new WalletCropImageServiceImpl(activity);
   }

   @Provides
   WalletStartPresenter provideWalletStartPresenter(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor, WalletAccessValidator walletAccessValidator,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletStartPresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor,
            walletAccessValidator, httpErrorHandlingUtil);
   }

   @Provides
   WalletProvisioningBlockedPresenter provideWalletProvisioningBlockedPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletProvisioningBlockedPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   WizardWelcomePresenter provideWizardWelcomePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WalletSocialInfoProvider socialInfoProvider,
         AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WizardWelcomePresenterImpl(navigator, smartCardInteractor, networkService, socialInfoProvider,
            analyticsInteractor, wizardInteractor);
   }

   @Provides
   WizardPowerOnPresenter provideWizardPowerOnPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         WalletBluetoothService walletBluetoothService, AnalyticsInteractor analyticsInteractor) {
      return new WizardPowerOnPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor,
            walletBluetoothService, analyticsInteractor);
   }

   @Provides
   WizardTermsPresenter provideWizardTermsPresenter(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor, @Named(JANET_WALLET) Janet janet) {
      return new WizardTermsPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor, janet);
   }

   @Provides
   WizardCheckingPresenter providesWizardCheckingPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         WalletBluetoothService walletBluetoothService) {
      return new WizardCheckingPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor, walletBluetoothService);
   }

   @Provides
   WizardSplashPresenter provideWizardSplashPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WizardSplashPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   WizardScanBarcodePresenter provideWizardScanBarcodePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         AnalyticsInteractor analyticsInteractor, PermissionDispatcher permissionDispatcher) {
      return new WizardScanBarcodePresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor,
            analyticsInteractor, permissionDispatcher);
   }

   @Provides
   PairKeyPresenter providePairKeyPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         AnalyticsInteractor analyticsInteractor) {
      return new PairKeyPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor, analyticsInteractor);
   }

   @Provides
   WizardManualInputPresenter provideWizardManualInputPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         WizardInteractor wizardInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WizardManualInputPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, wizardInteractor, httpErrorHandlingUtil);
   }

   @Provides
   ExistingDeviceDetectPresenter provideExistingDeviceDetectPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new ExistingDeviceDetectPresenterImpl(navigator, smartCardInteractor, networkService,
            wizardInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WizardEditProfilePresenter provideWizardEditProfilePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor,
         WalletSocialInfoProvider socialInfoProvider, SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletFeatureHelper walletFeatureHelper) {
      return new WizardEditProfilePresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor,
            analyticsInteractor, socialInfoProvider, smartCardUserDataInteractor, walletFeatureHelper);
   }

   @Provides
   WizardUploadProfilePresenter provideWizardUploadProfilePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WizardUploadProfilePresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor,
            analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   WizardAssignUserPresenter provideWizardAssignUserPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WizardAssignUserPresenterImpl(navigator, smartCardInteractor, networkService,
            wizardInteractor, recordInteractor, analyticsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WalletPinIsSetPresenter provideWalletPinIsSetPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WalletPinIsSetPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, wizardInteractor);
   }

   @Provides
   PinProposalPresenter providesPinProposalPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor) {
      return new PinProposalPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor);
   }

   @Provides
   EnterPinPresenter provideEnterPinPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor) {
      return new EnterPinPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor, analyticsInteractor);
   }

   @Provides
   PinSetSuccessPresenter providePinSetSuccessPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new PinSetSuccessPresenterImpl(navigator, smartCardInteractor, networkService);
   }

   @Provides
   SyncRecordsPresenter provideSyncRecordsPresenter(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, AnalyticsInteractor analyticsInteractor) {
      return new SyncRecordsPresenterImpl(navigator, smartCardInteractor, networkService, recordInteractor, analyticsInteractor);
   }

   @Provides
   PaymentSyncFinishPresenter providePaymentSyncFinishPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WizardInteractor wizardInteractor, AnalyticsInteractor analyticsInteractor) {
      return new PaymentSyncFinishPresenterImpl(navigator, smartCardInteractor, networkService,
            wizardInteractor, analyticsInteractor);
   }

   @Provides
   CardListPresenter provideCardListPresenter(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor,
         NavigationDrawerPresenter navigationDrawerPresenter, WalletFeatureHelper walletFeatureHelper) {
      return new CardListPresenterImpl(navigator, smartCardInteractor, networkService, recordInteractor,
            firmwareInteractor, analyticsInteractor, factoryResetInteractor,
            navigationDrawerPresenter, walletFeatureHelper);
   }

   @Provides
   WalletSettingsPresenter provideWalletSettingsPresenter(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSettingsPresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   WalletGeneralSettingsPresenter providesWalletGeneralSettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         FactoryResetInteractor factoryResetInteractor, AnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletGeneralSettingsPresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor,
            factoryResetInteractor, analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   FactoryResetPresenter provideFactoryResetPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new FactoryResetPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor, factoryResetInteractor);
   }

   @Provides
   FactoryResetSuccessPresenter provideFactoryResetSuccessPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new FactoryResetSuccessPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   NewCardPowerOnPresenter provideNewCardPowerOnPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, WalletBluetoothService walletBluetoothService) {
      return new NewCardPowerOnPresenterImpl(navigator, smartCardInteractor, networkService, factoryResetInteractor,
            analyticsInteractor, walletBluetoothService);
   }

   @Provides
   PreCheckNewCardPresenter providePreCheckNewCardPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletBluetoothService walletBluetoothService) {
      return new PreCheckNewCardPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor,
            factoryResetInteractor, walletBluetoothService);
   }

   @Provides
   ExistingCardDetectPresenter provideExistingCardDetectPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new ExistingCardDetectPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor,
            factoryResetInteractor, httpErrorHandlingUtil);
   }

   @Provides
   EnterPinUnassignPresenter provideEnterPinUnassignPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new EnterPinUnassignPresenterImpl(navigator, smartCardInteractor, networkService,
            factoryResetInteractor, analyticsInteractor);
   }

   @Provides
   UnassignSuccessPresenter provideUnassignSuccessPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new UnassignSuccessPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor);
   }

   @Provides
   AboutPresenter provideAboutPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor) {
      return new AboutPresenterImpl(navigator, smartCardInteractor, networkService, recordInteractor, analyticsInteractor);
   }

   @Provides
   WalletSecuritySettingsPresenter provideWalletSecuritySettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSecuritySettingsPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory, walletFeatureHelper);
   }

   @Provides
   WalletHelpSettingsPresenter provideWalletHelpSettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new WalletHelpSettingsPresenterImpl(navigator, smartCardInteractor, networkService);
   }

   @Provides
   WalletSettingsProfilePresenter provideWalletSettingsProfilePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletSocialInfoProvider socialInfoProvider) {
      return new WalletSettingsProfilePresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, smartCardUserDataInteractor, socialInfoProvider);
   }

   @Provides
   WalletCustomerSupportSettingsPresenter provideWalletCustomerSupportSettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WalletSettingsInteractor walletSettingsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletCustomerSupportSettingsPresenterImpl(navigator, smartCardInteractor, networkService,
            walletSettingsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   LostCardPresenter provideLostCardPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         PermissionDispatcher permissionDispatcher, SmartCardLocationInteractor smartCardLocationInteractor,
         WalletDetectLocationService walletDetectLocationService, Activity activity, AnalyticsInteractor analyticsInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      //noinspection all
      return new LostCardPresenterImpl(navigator, smartCardInteractor, networkService, permissionDispatcher,
            smartCardLocationInteractor, walletDetectLocationService,
            (LocationScreenComponent) activity.getSystemService(LocationScreenComponent.COMPONENT_NAME), analyticsInteractor,
            httpErrorHandlingUtil);
   }

   @Provides
   WalletAutoClearCardsPresenter provideWalletAutoClearCardsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletAutoClearCardsPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletDisableDefaultCardPresenter provideWalletDisableDefaultCardPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletDisableDefaultCardPresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletOfflineModeSettingsPresenter provideWalletOfflineModeSettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletOfflineModeSettingsPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   DisplayOptionsSettingsPresenter provideDisplayOptionsSettingsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new DisplayOptionsSettingsPresenterImpl(navigator, smartCardInteractor, smartCardUserDataInteractor,
            networkService, analyticsInteractor);
   }

   @Provides
   WalletNewFirmwareAvailablePresenter provideWalletNewFirmwareAvailablePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletNewFirmwareAvailablePresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WalletPuckConnectionPresenter provideWalletPuckConnectionPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new WalletPuckConnectionPresenterImpl(navigator, smartCardInteractor, networkService);
   }

   @Provides
   WalletDownloadFirmwarePresenter provideWalletDownloadFirmwarePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FirmwareInteractor firmwareInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletDownloadFirmwarePresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor,
            firmwareInteractor, errorHandlerFactory);
   }

   @Provides
   WalletFirmwareChecksPresenter provideWalletFirmwareChecksPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WalletBluetoothService walletBluetoothService,
         FirmwareInteractor firmwareInteractor, AnalyticsInteractor analyticsInteractor) {
      return new WalletFirmwareChecksPresenterImpl(navigator, smartCardInteractor, networkService,
            walletBluetoothService, firmwareInteractor, analyticsInteractor);
   }

   @Provides
   WalletInstallFirmwarePresenter provideWalletInstallFirmwarePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletInstallFirmwarePresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletSuccessInstallFirmwarePresenter provideWalletSuccessInstallFirmwarePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletSuccessInstallFirmwarePresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   ForceUpdatePowerOnPresenter provideForceUpdatePowerOnPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         WalletBluetoothService walletBluetoothService) {
      return new ForceUpdatePowerOnPresenterImpl(navigator, smartCardInteractor, networkService, wizardInteractor,
            walletBluetoothService);
   }

   @Provides
   ForcePairKeyPresenter provideForcePairKeyPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         ErrorHandlerFactory errorHandlerFactory) {
      return new ForcePairKeyPresenterImpl(navigator, smartCardInteractor, networkService, firmwareInteractor, errorHandlerFactory);
   }

   @Provides
   StartFirmwareInstallPresenter provideStartFirmwareInstallPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         ErrorHandlerFactory errorHandlerFactory, FirmwareInteractor firmwareInteractor) {
      return new StartFirmwareInstallPresenterImpl(navigator, smartCardInteractor, networkService,
            errorHandlerFactory, firmwareInteractor);
   }

   @Provides
   WalletUpToDateFirmwarePresenter providesWalletUpToDateFirmwarePresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor) {
      return new WalletUpToDateFirmwarePresenterImpl(navigator, smartCardInteractor, networkService,
            analyticsInteractor);
   }

   @Provides
   WalletHelpVideoPresenter provideWalletHelpVideoPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, MemberVideosInteractor memberVideosInteractor,
         CachedEntityInteractor cachedEntityInteractor, CachedEntityDelegate cachedEntityDelegate, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletHelpVideoPresenterImpl(navigator, smartCardInteractor, networkService, memberVideosInteractor,
            cachedEntityInteractor, cachedEntityDelegate, httpErrorHandlingUtil);
   }

   @Provides
   WalletHelpDocumentsPresenter provideWalletHelpDocumentsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         DocumentsInteractor documentsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletHelpDocumentsPresenterImpl(navigator, smartCardInteractor, networkService,
            documentsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   HelpDocumentDetailPresenter provideHelpDocumentDetailPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new HelpDocumentDetailPresenterImpl(navigator, smartCardInteractor, networkService);
   }

   @Provides
   SendFeedbackPresenter providesSendFeedbackPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaInteractor mediaInteractor) {
      return new SendFeedbackPresenterImpl(navigator, smartCardInteractor, networkService,
            feedbackInteractor, walletSettingsInteractor, mediaInteractor);
   }

   @Provides
   PaymentFeedbackPresenter providePaymentFeedbackPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaInteractor mediaInteractor) {
      return new PaymentFeedbackPresenterImpl(navigator, smartCardInteractor, networkService,
            feedbackInteractor, walletSettingsInteractor, mediaInteractor);
   }

   @Provides
   WizardChargingPresenter providesWizardChargingPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WizardChargingPresenterImpl(navigator, smartCardInteractor, networkService, recordInteractor,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   ConnectionErrorPresenter provideConnectionErrorPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new ConnectionErrorPresenterImpl(navigator, smartCardInteractor, networkService);
   }

   @Provides
   AddCardDetailsPresenter provideAddCardDetailsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor, WizardInteractor wizardInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new AddCardDetailsPresenterImpl(navigator, smartCardInteractor, networkService, analyticsInteractor,
            recordInteractor, wizardInteractor, httpErrorHandlingUtil);
   }

   @Provides
   CardDetailsPresenter provideCardDetailsPresenter(Navigator navigator,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor) {
      return new CardDetailsPresenterImpl(navigator, smartCardInteractor, networkService, recordInteractor, analyticsInteractor);
   }
}
