package com.worldventures.wallet.di;

import android.app.Activity;
import android.content.Context;

import com.bluelinelabs.conductor.Router;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.infopages.service.CancelableFeedbackAttachmentsManager;
import com.worldventures.core.modules.infopages.service.DocumentsInteractor;
import com.worldventures.core.modules.infopages.service.FeedbackInteractor;
import com.worldventures.core.modules.picker.service.MediaPickerInteractor;
import com.worldventures.core.modules.video.service.MemberVideosInteractor;
import com.worldventures.core.service.CachedEntityDelegate;
import com.worldventures.core.service.CachedEntityInteractor;
import com.worldventures.core.ui.util.permission.PermissionDispatcher;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.SmartCardSyncManager;
import com.worldventures.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.wallet.service.WalletAccessValidator;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.WalletCropImageService;
import com.worldventures.wallet.service.WalletCropImageServiceImpl;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.wallet.service.location.WalletDetectLocationService;
import com.worldventures.wallet.service.lostcard.LocationTrackingManager;
import com.worldventures.wallet.ui.WalletActivity;
import com.worldventures.wallet.ui.common.LocationScreenComponent;
import com.worldventures.wallet.ui.common.WalletNavigationDelegate;
import com.worldventures.wallet.ui.common.activity.WalletActivityPresenter;
import com.worldventures.wallet.ui.common.activity.WalletActivityPresenterImpl;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegateImpl;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegate;
import com.worldventures.wallet.ui.common.base.WalletNetworkDelegateImpl;
import com.worldventures.wallet.ui.common.navigation.CoreNavigator;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.common.navigation.NavigatorImpl;
import com.worldventures.wallet.ui.dashboard.CardListPresenter;
import com.worldventures.wallet.ui.dashboard.impl.CardListPresenterImpl;
import com.worldventures.wallet.ui.dashboard.impl.CardListScreenImpl;
import com.worldventures.wallet.ui.provisioning_blocked.WalletProvisioningBlockedPresenter;
import com.worldventures.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedPresenterImpl;
import com.worldventures.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedScreenImpl;
import com.worldventures.wallet.ui.records.add.AddCardDetailsPresenter;
import com.worldventures.wallet.ui.records.add.impl.AddCardDetailsPresenterImpl;
import com.worldventures.wallet.ui.records.add.impl.AddCardDetailsScreenImpl;
import com.worldventures.wallet.ui.records.connectionerror.ConnectionErrorPresenter;
import com.worldventures.wallet.ui.records.connectionerror.impl.ConnectionErrorPresenterImpl;
import com.worldventures.wallet.ui.records.connectionerror.impl.ConnectionErrorScreenImpl;
import com.worldventures.wallet.ui.records.detail.CardDetailsPresenter;
import com.worldventures.wallet.ui.records.detail.impl.CardDetailsPresenterImpl;
import com.worldventures.wallet.ui.records.detail.impl.CardDetailsScreenImpl;
import com.worldventures.wallet.ui.records.swiping.WizardChargingPresenter;
import com.worldventures.wallet.ui.records.swiping.impl.WizardChargingPresenterImpl;
import com.worldventures.wallet.ui.records.swiping.impl.WizardChargingScreenImpl;
import com.worldventures.wallet.ui.settings.WalletSettingsPresenter;
import com.worldventures.wallet.ui.settings.general.WalletGeneralSettingsPresenter;
import com.worldventures.wallet.ui.settings.general.about.AboutPresenter;
import com.worldventures.wallet.ui.settings.general.about.impl.AboutPresenterImpl;
import com.worldventures.wallet.ui.settings.general.about.impl.AboutScreenImpl;
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.download.WalletDownloadFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwarePresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.install.WalletInstallFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwarePresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.WalletSuccessInstallFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwarePresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.WalletNewFirmwareAvailablePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailablePresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailableScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.WalletFirmwareChecksPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksPresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.WalletPuckConnectionPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionPresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.ForcePairKeyPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyPresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.ForceUpdatePowerOnPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnPresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.start.StartFirmwareInstallPresenter;
import com.worldventures.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallPresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.WalletUpToDateFirmwarePresenter;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwarePresenterImpl;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.impl.WalletGeneralSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.general.impl.WalletGeneralSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.check.PreCheckNewCardPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardPresenterImpl;
import com.worldventures.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.detection.ExistingCardDetectPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectPresenterImpl;
import com.worldventures.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignPresenterImpl;
import com.worldventures.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.NewCardPowerOnPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnPresenterImpl;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.success.UnassignSuccessPresenter;
import com.worldventures.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessPresenterImpl;
import com.worldventures.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessScreenImpl;
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.wallet.ui.settings.general.profile.impl.WalletSettingsProfilePresenterImpl;
import com.worldventures.wallet.ui.settings.general.profile.impl.WalletSettingsProfileScreenImpl;
import com.worldventures.wallet.ui.settings.general.reset.FactoryResetPresenter;
import com.worldventures.wallet.ui.settings.general.reset.impl.FactoryResetPresenterImpl;
import com.worldventures.wallet.ui.settings.general.reset.impl.FactoryResetScreenImpl;
import com.worldventures.wallet.ui.settings.general.reset.success.FactoryResetSuccessPresenter;
import com.worldventures.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessPresenterImpl;
import com.worldventures.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessScreenImpl;
import com.worldventures.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter;
import com.worldventures.wallet.ui.settings.help.documents.doc.HelpDocumentDetailPresenter;
import com.worldventures.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailPresenterImpl;
import com.worldventures.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailScreenImpl;
import com.worldventures.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsPresenterImpl;
import com.worldventures.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsScreenImpl;
import com.worldventures.wallet.ui.settings.help.feedback.base.impl.FeedbackAttachmentsPresenterDelegateImpl;
import com.worldventures.wallet.ui.settings.help.feedback.payment.PaymentFeedbackPresenter;
import com.worldventures.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackPresenterImpl;
import com.worldventures.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackScreenImpl;
import com.worldventures.wallet.ui.settings.help.feedback.regular.SendFeedbackPresenter;
import com.worldventures.wallet.ui.settings.help.feedback.regular.impl.SendFeedbackPresenterImpl;
import com.worldventures.wallet.ui.settings.help.feedback.regular.impl.SendFeedbackScreenImpl;
import com.worldventures.wallet.ui.settings.help.impl.WalletHelpSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.help.impl.WalletHelpSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.help.support.WalletCustomerSupportSettingsPresenter;
import com.worldventures.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.help.video.WalletHelpVideoPresenter;
import com.worldventures.wallet.ui.settings.help.video.impl.WalletHelpVideoDelegate;
import com.worldventures.wallet.ui.settings.help.video.impl.WalletHelpVideoPresenterImpl;
import com.worldventures.wallet.ui.settings.help.video.impl.WalletHelpVideoScreenImpl;
import com.worldventures.wallet.ui.settings.impl.WalletSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.impl.WalletSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.security.WalletSecuritySettingsPresenter;
import com.worldventures.wallet.ui.settings.security.clear.common.items.AutoClearSmartCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.common.items.DisableDefaultCardItemProvider;
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardPresenter;
import com.worldventures.wallet.ui.settings.security.clear.default_card.impl.WalletDisableDefaultCardPresenterImpl;
import com.worldventures.wallet.ui.settings.security.clear.default_card.impl.WalletDisableDefaultCardScreenImpl;
import com.worldventures.wallet.ui.settings.security.clear.records.WalletAutoClearCardsPresenter;
import com.worldventures.wallet.ui.settings.security.clear.records.impl.WalletAutoClearCardsPresenterImpl;
import com.worldventures.wallet.ui.settings.security.clear.records.impl.WalletAutoClearCardsScreenImpl;
import com.worldventures.wallet.ui.settings.security.impl.WalletSecuritySettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.security.impl.WalletSecuritySettingsScreenImpl;
import com.worldventures.wallet.ui.settings.security.lostcard.LostCardPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.MapPresenter;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.LostCardPresenterImpl;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.LostCardScreenImpl;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.MapPresenterImpl;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.MapScreenImpl;
import com.worldventures.wallet.ui.settings.security.offline_mode.WalletOfflineModeSettingsPresenter;
import com.worldventures.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsPresenterImpl;
import com.worldventures.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsScreenImpl;
import com.worldventures.wallet.ui.start.WalletStartPresenter;
import com.worldventures.wallet.ui.start.impl.WalletStartPresenterImpl;
import com.worldventures.wallet.ui.start.impl.WalletStartScreenImpl;
import com.worldventures.wallet.ui.wizard.assign.WizardAssignUserPresenter;
import com.worldventures.wallet.ui.wizard.assign.impl.WizardAssignUserPresenterImpl;
import com.worldventures.wallet.ui.wizard.assign.impl.WizardAssignUserScreenImpl;
import com.worldventures.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.wallet.ui.wizard.checking.impl.WizardCheckingPresenterImpl;
import com.worldventures.wallet.ui.wizard.checking.impl.WizardCheckingScreenImpl;
import com.worldventures.wallet.ui.wizard.input.helper.InputAnalyticsDelegate;
import com.worldventures.wallet.ui.wizard.input.helper.InputBarcodeDelegateImpl;
import com.worldventures.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.wallet.ui.wizard.input.manual.impl.WizardManualInputPresenterImpl;
import com.worldventures.wallet.ui.wizard.input.manual.impl.WizardManualInputScreenImpl;
import com.worldventures.wallet.ui.wizard.input.scanner.WizardScanBarcodePresenter;
import com.worldventures.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodePresenterImpl;
import com.worldventures.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodeScreenImpl;
import com.worldventures.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.wallet.ui.wizard.pairkey.impl.PairKeyPresenterImpl;
import com.worldventures.wallet.ui.wizard.pairkey.impl.PairKeyScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetPresenterImpl;
import com.worldventures.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.wallet.ui.wizard.pin.enter.impl.EnterPinPresenterImpl;
import com.worldventures.wallet.ui.wizard.pin.enter.impl.EnterPinScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.wallet.ui.wizard.pin.proposal.impl.PinProposalPresenterImpl;
import com.worldventures.wallet.ui.wizard.pin.proposal.impl.PinProposalScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.wallet.ui.wizard.pin.success.impl.PinSetSuccessPresenterImpl;
import com.worldventures.wallet.ui.wizard.pin.success.impl.PinSetSuccessScreenImpl;
import com.worldventures.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.wallet.ui.wizard.power_on.impl.WizardPowerOnPresenterImpl;
import com.worldventures.wallet.ui.wizard.power_on.impl.WizardPowerOnScreenImpl;
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.wallet.ui.wizard.profile.impl.WizardEditProfilePresenterImpl;
import com.worldventures.wallet.ui.wizard.profile.impl.WizardEditProfileScreenImpl;
import com.worldventures.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.wallet.ui.wizard.profile.restore.impl.WizardUploadProfilePresenterImpl;
import com.worldventures.wallet.ui.wizard.profile.restore.impl.WizardUploadProfileScreenImpl;
import com.worldventures.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishPresenterImpl;
import com.worldventures.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishScreenImpl;
import com.worldventures.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.wallet.ui.wizard.records.sync.impl.SyncRecordsPresenterImpl;
import com.worldventures.wallet.ui.wizard.records.sync.impl.SyncRecordsScreenImpl;
import com.worldventures.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.wallet.ui.wizard.splash.impl.WizardSplashPresenterImpl;
import com.worldventures.wallet.ui.wizard.splash.impl.WizardSplashScreenImpl;
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.wallet.ui.wizard.termsandconditionals.impl.WizardTermsPresenterImpl;
import com.worldventures.wallet.ui.wizard.termsandconditionals.impl.WizardTermsScreenImpl;
import com.worldventures.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectPresenterImpl;
import com.worldventures.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectScreenImpl;
import com.worldventures.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.wallet.ui.wizard.welcome.impl.WizardWelcomePresenterImpl;
import com.worldventures.wallet.ui.wizard.welcome.impl.WizardWelcomeScreenImpl;
import com.worldventures.wallet.util.WalletBuildConfigHelper;
import com.worldventures.wallet.util.WalletFeatureHelper;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

@Module(injects = {
      WalletActivity.class,
      WalletStartScreenImpl.class,
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
      MapScreenImpl.class,
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

   @Singleton
   @Provides
   Router provideRouter(Activity activity) {
      return ((WalletActivity) activity).getRouter();
   }

   @Singleton
   @Provides
   Navigator provideConductorNavigator(Lazy<Router> router, CoreNavigator coreNavigator, WalletBuildConfigHelper walletBuildConfigHelper) {
      return new NavigatorImpl(router, coreNavigator, walletBuildConfigHelper);
   }

   @Provides
   @Singleton
   WalletCropImageService provideWalletCropImageDelegate() {
      return new WalletCropImageServiceImpl();
   }

   @Provides
   WalletNetworkDelegate provideWalletNetworkDelegate(WalletNetworkService walletNetworkService) {
      return new WalletNetworkDelegateImpl(walletNetworkService);
   }

   @Provides
   WalletDeviceConnectionDelegate provideDeviceConnectionDelegate(SmartCardInteractor smartCardInteractor) {
      return new WalletDeviceConnectionDelegateImpl(smartCardInteractor);
   }

   @Provides
   WalletActivityPresenter provideWalletActivityPresenter(SmartCardSyncManager smartCardSyncManager,
         SmartCardAnalyticErrorHandler smartCardAnalyticErrorHandler, SmartCardInteractor interactor,
         WalletBluetoothService bluetoothService, AuthInteractor authInteractor) {
      return new WalletActivityPresenterImpl(smartCardSyncManager, smartCardAnalyticErrorHandler,
            interactor, bluetoothService, authInteractor);
   }

   @Provides
   WalletStartPresenter provideWalletStartPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor, WalletAccessValidator walletAccessValidator,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletStartPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
            walletAccessValidator, httpErrorHandlingUtil);
   }

   @Provides
   WalletProvisioningBlockedPresenter provideWalletProvisioningBlockedPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletProvisioningBlockedPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor);
   }

   @Provides
   WizardWelcomePresenter provideWizardWelcomePresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletSocialInfoProvider socialInfoProvider,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WizardWelcomePresenterImpl(navigator, deviceConnectionDelegate, socialInfoProvider,
            analyticsInteractor, wizardInteractor);
   }

   @Provides
   WizardPowerOnPresenter provideWizardPowerOnPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate,
         WalletBluetoothService walletBluetoothService, WalletAnalyticsInteractor analyticsInteractor) {
      return new WizardPowerOnPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService, analyticsInteractor);
   }

   @Provides
   WizardTermsPresenter provideWizardTermsPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WizardTermsPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, wizardInteractor);
   }

   @Provides
   WizardCheckingPresenter providesWizardCheckingPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, WalletBluetoothService walletBluetoothService) {
      return new WizardCheckingPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService);
   }

   @Provides
   WizardSplashPresenter provideWizardSplashPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new WizardSplashPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor);
   }

   @Provides
   WizardScanBarcodePresenter provideWizardScanBarcodePresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         PermissionDispatcher permissionDispatcher, SmartCardInteractor smartCardInteractor) {
      return new WizardScanBarcodePresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher,
            new InputBarcodeDelegateImpl(navigator, wizardInteractor, InputAnalyticsDelegate.Companion.createForScannerScreen(analyticsInteractor), smartCardInteractor));
   }

   @Provides
   PairKeyPresenter providePairKeyPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WizardInteractor wizardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new PairKeyPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, wizardInteractor, analyticsInteractor);
   }

   @Provides
   WizardManualInputPresenter provideWizardManualInputPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor,
         SmartCardInteractor smartCardInteractor) {
      return new WizardManualInputPresenterImpl(navigator, deviceConnectionDelegate,
            analyticsInteractor, new InputBarcodeDelegateImpl(navigator, wizardInteractor,
            InputAnalyticsDelegate.Companion.createForManualInputScreen(analyticsInteractor), smartCardInteractor));
   }

   @Provides
   ExistingDeviceDetectPresenter provideExistingDeviceDetectPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new ExistingDeviceDetectPresenterImpl(navigator, deviceConnectionDelegate,
            wizardInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WizardEditProfilePresenter provideWizardEditProfilePresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor,
         WalletSocialInfoProvider socialInfoProvider, SmartCardUserDataInteractor smartCardUserDataInteractor) {
      return new WizardEditProfilePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, wizardInteractor,
            analyticsInteractor, socialInfoProvider, smartCardUserDataInteractor);
   }

   @Provides
   WizardUploadProfilePresenter provideWizardUploadProfilePresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WizardUploadProfilePresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            wizardInteractor, analyticsInteractor);
   }

   @Provides
   WizardAssignUserPresenter provideWizardAssignUserPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WizardInteractor wizardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil,
         WalletFeatureHelper walletFeatureHelper) {
      return new WizardAssignUserPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            wizardInteractor, recordInteractor, analyticsInteractor, httpErrorHandlingUtil, walletFeatureHelper);
   }

   @Provides
   WalletPinIsSetPresenter provideWalletPinIsSetPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, WizardInteractor wizardInteractor) {
      return new WalletPinIsSetPresenterImpl(navigator, deviceConnectionDelegate,
            analyticsInteractor, wizardInteractor);
   }

   @Provides
   PinProposalPresenter providesPinProposalPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor) {
      return new PinProposalPresenterImpl(navigator, deviceConnectionDelegate, wizardInteractor);
   }

   @Provides
   EnterPinPresenter provideEnterPinPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new EnterPinPresenterImpl(navigator, deviceConnectionDelegate, wizardInteractor, analyticsInteractor);
   }

   @Provides
   PinSetSuccessPresenter providePinSetSuccessPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      return new PinSetSuccessPresenterImpl(navigator, deviceConnectionDelegate);
   }

   @Provides
   SyncRecordsPresenter provideSyncRecordsPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new SyncRecordsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, recordInteractor, analyticsInteractor);
   }

   @Provides
   PaymentSyncFinishPresenter providePaymentSyncFinishPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new PaymentSyncFinishPresenterImpl(navigator, deviceConnectionDelegate,
            wizardInteractor, analyticsInteractor);
   }

   @Provides
   CardListPresenter provideCardListPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, SmartCardInteractor smartCardInteractor,
         RecordInteractor recordInteractor, FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor,
         WalletNavigationDelegate navigationDelegate, WalletFeatureHelper walletFeatureHelper,
         LocationTrackingManager locationTrackingManager) {
      return new CardListPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate,
            smartCardInteractor, recordInteractor,
            firmwareInteractor, analyticsInteractor, factoryResetInteractor,
            navigationDelegate, walletFeatureHelper, locationTrackingManager);
   }

   @Provides
   WalletSettingsPresenter provideWalletSettingsPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
            analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   WalletGeneralSettingsPresenter providesWalletGeneralSettingsPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FirmwareInteractor firmwareInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletAnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletGeneralSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, firmwareInteractor,
            factoryResetInteractor, analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   FactoryResetPresenter provideFactoryResetPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new FactoryResetPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor, factoryResetInteractor);
   }

   @Provides
   FactoryResetSuccessPresenter provideFactoryResetSuccessPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new FactoryResetSuccessPresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor);
   }

   @Provides
   NewCardPowerOnPresenter provideNewCardPowerOnPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, FactoryResetInteractor factoryResetInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletBluetoothService walletBluetoothService) {
      return new NewCardPowerOnPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            factoryResetInteractor, analyticsInteractor, walletBluetoothService);
   }

   @Provides
   PreCheckNewCardPresenter providePreCheckNewCardPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletBluetoothService walletBluetoothService) {
      return new PreCheckNewCardPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
            factoryResetInteractor, walletBluetoothService);
   }

   @Provides
   ExistingCardDetectPresenter provideExistingCardDetectPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, FactoryResetInteractor factoryResetInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new ExistingCardDetectPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
            factoryResetInteractor, httpErrorHandlingUtil);
   }

   @Provides
   EnterPinUnassignPresenter provideEnterPinUnassignPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new EnterPinUnassignPresenterImpl(navigator, deviceConnectionDelegate,
            factoryResetInteractor, analyticsInteractor);
   }

   @Provides
   UnassignSuccessPresenter provideUnassignSuccessPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new UnassignSuccessPresenterImpl(navigator, deviceConnectionDelegate,
            analyticsInteractor);
   }

   @Provides
   AboutPresenter provideAboutPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, RecordInteractor recordInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new AboutPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            recordInteractor, analyticsInteractor);
   }

   @Provides
   WalletSecuritySettingsPresenter provideWalletSecuritySettingsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSecuritySettingsPresenterImpl(navigator, deviceConnectionDelegate,
            smartCardInteractor, analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   WalletHelpSettingsPresenter provideWalletHelpSettingsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      return new WalletHelpSettingsPresenterImpl(navigator, deviceConnectionDelegate);
   }

   @Provides
   WalletSettingsProfilePresenter provideWalletSettingsProfilePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletSocialInfoProvider socialInfoProvider) {
      return new WalletSettingsProfilePresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor,
            smartCardInteractor, smartCardUserDataInteractor, socialInfoProvider);
   }

   @Provides
   WalletCustomerSupportSettingsPresenter provideWalletCustomerSupportSettingsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletSettingsInteractor walletSettingsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletCustomerSupportSettingsPresenterImpl(navigator, deviceConnectionDelegate,
            walletSettingsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   LostCardPresenter provideLostCardPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         PermissionDispatcher permissionDispatcher, SmartCardLocationInteractor smartCardLocationInteractor,
         WalletDetectLocationService walletDetectLocationService, Activity activity, WalletAnalyticsInteractor analyticsInteractor) {
      //noinspection all
      return new LostCardPresenterImpl(navigator, deviceConnectionDelegate, permissionDispatcher,
            smartCardLocationInteractor, walletDetectLocationService,
            (LocationScreenComponent) activity.getSystemService(LocationScreenComponent.COMPONENT_NAME), analyticsInteractor);
   }

   @Provides
   MapPresenter provideMapPresenter(SmartCardLocationInteractor smartCardLocationInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new MapPresenterImpl(smartCardLocationInteractor, analyticsInteractor);
   }

   @Provides
   DisableDefaultCardItemProvider provideDisableDefaultCardItemProvider(Context context) {
      return new DisableDefaultCardItemProvider(context);
   }

   @Provides
   AutoClearSmartCardItemProvider provideAutoClearSmartCardItemProvider(Context context) {
      return new AutoClearSmartCardItemProvider(context);
   }

   @Provides
   WalletAutoClearCardsPresenter provideWalletAutoClearCardsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, AutoClearSmartCardItemProvider itemProvider) {
      return new WalletAutoClearCardsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            analyticsInteractor, itemProvider);
   }

   @Provides
   WalletDisableDefaultCardPresenter provideWalletDisableDefaultCardPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, DisableDefaultCardItemProvider itemProvider) {
      return new WalletDisableDefaultCardPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            analyticsInteractor, itemProvider);
   }

   @Provides
   WalletOfflineModeSettingsPresenter provideWalletOfflineModeSettingsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletNetworkDelegate networkDelegate, WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletOfflineModeSettingsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            networkDelegate, analyticsInteractor);
   }

   @Provides
   DisplayOptionsSettingsPresenter provideDisplayOptionsSettingsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         SmartCardUserDataInteractor smartCardUserDataInteractor, WalletAnalyticsInteractor analyticsInteractor,
         WalletSocialInfoProvider socialInfoProvider) {
      return new DisplayOptionsSettingsPresenterImpl(navigator, deviceConnectionDelegate,
            new WalletProfileDelegate(smartCardUserDataInteractor, smartCardInteractor, analyticsInteractor),
            smartCardInteractor, socialInfoProvider);
   }

   @Provides
   WalletNewFirmwareAvailablePresenter provideWalletNewFirmwareAvailablePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletNewFirmwareAvailablePresenterImpl(navigator, deviceConnectionDelegate,
            firmwareInteractor, analyticsInteractor);
   }

   @Provides
   WalletPuckConnectionPresenter provideWalletPuckConnectionPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor) {
      return new WalletPuckConnectionPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor);
   }

   @Provides
   WalletDownloadFirmwarePresenter provideWalletDownloadFirmwarePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor, FirmwareInteractor firmwareInteractor) {
      return new WalletDownloadFirmwarePresenterImpl(navigator, deviceConnectionDelegate,
            analyticsInteractor, firmwareInteractor);
   }

   @Provides
   WalletFirmwareChecksPresenter provideWalletFirmwareChecksPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletBluetoothService walletBluetoothService,
         FirmwareInteractor firmwareInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletFirmwareChecksPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            walletBluetoothService, firmwareInteractor, analyticsInteractor);
   }

   @Provides
   WalletInstallFirmwarePresenter provideWalletInstallFirmwarePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor, WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletInstallFirmwarePresenterImpl(navigator, deviceConnectionDelegate,
            firmwareInteractor, analyticsInteractor);
   }

   @Provides
   WalletSuccessInstallFirmwarePresenter provideWalletSuccessInstallFirmwarePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletSuccessInstallFirmwarePresenterImpl(navigator, deviceConnectionDelegate, analyticsInteractor);
   }

   @Provides
   ForceUpdatePowerOnPresenter provideForceUpdatePowerOnPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, WalletNetworkDelegate networkDelegate,
         WalletBluetoothService walletBluetoothService) {
      return new ForceUpdatePowerOnPresenterImpl(navigator, deviceConnectionDelegate, networkDelegate, walletBluetoothService);
   }

   @Provides
   ForcePairKeyPresenter provideForcePairKeyPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor) {
      return new ForcePairKeyPresenterImpl(navigator, deviceConnectionDelegate, firmwareInteractor);
   }

   @Provides
   StartFirmwareInstallPresenter provideStartFirmwareInstallPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FirmwareInteractor firmwareInteractor) {
      return new StartFirmwareInstallPresenterImpl(navigator, deviceConnectionDelegate,
            firmwareInteractor);
   }

   @Provides
   WalletUpToDateFirmwarePresenter providesWalletUpToDateFirmwarePresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new WalletUpToDateFirmwarePresenterImpl(navigator, deviceConnectionDelegate,
            smartCardInteractor, analyticsInteractor);
   }

   @Provides
   WalletHelpVideoPresenter provideWalletHelpVideoPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         MemberVideosInteractor memberVideosInteractor,
         CachedEntityInteractor cachedEntityInteractor, CachedEntityDelegate cachedEntityDelegate, Context context) {
      return new WalletHelpVideoPresenterImpl(navigator, deviceConnectionDelegate, memberVideosInteractor,
            cachedEntityInteractor, cachedEntityDelegate, new WalletHelpVideoDelegate(context));
   }

   @Provides
   WalletHelpDocumentsPresenter provideWalletHelpDocumentsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         DocumentsInteractor documentsInteractor) {
      return new WalletHelpDocumentsPresenterImpl(navigator, deviceConnectionDelegate, documentsInteractor);
   }

   @Provides
   HelpDocumentDetailPresenter provideHelpDocumentDetailPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate) {
      return new HelpDocumentDetailPresenterImpl(navigator, deviceConnectionDelegate);
   }

   @Provides
   SendFeedbackPresenter providesSendFeedbackPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaPickerInteractor mediaPickerInteractor) {
      return new SendFeedbackPresenterImpl(navigator, deviceConnectionDelegate,
            new FeedbackAttachmentsPresenterDelegateImpl(mediaPickerInteractor, feedbackInteractor,
                  new CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe())),
            walletSettingsInteractor);
   }

   @Provides
   PaymentFeedbackPresenter providePaymentFeedbackPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaPickerInteractor mediaPickerInteractor) {
      return new PaymentFeedbackPresenterImpl(navigator, deviceConnectionDelegate,
            new FeedbackAttachmentsPresenterDelegateImpl(mediaPickerInteractor, feedbackInteractor,
                  new CancelableFeedbackAttachmentsManager(feedbackInteractor.uploadAttachmentPipe())),
            walletSettingsInteractor);
   }

   @Provides
   WizardChargingPresenter providesWizardChargingPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      return new WizardChargingPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor);
   }

   @Provides
   ConnectionErrorPresenter provideConnectionErrorPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor) {
      return new ConnectionErrorPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor);
   }

   @Provides
   AddCardDetailsPresenter provideAddCardDetailsPresenter(Navigator navigator,
         WalletDeviceConnectionDelegate deviceConnectionDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor, WizardInteractor wizardInteractor) {
      return new AddCardDetailsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor, analyticsInteractor,
            recordInteractor, wizardInteractor);
   }

   @Provides
   CardDetailsPresenter provideCardDetailsPresenter(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         WalletNetworkDelegate networkDelegate, SmartCardInteractor smartCardInteractor,
         WalletAnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor) {
      return new CardDetailsPresenterImpl(navigator, deviceConnectionDelegate, smartCardInteractor,
            networkDelegate, recordInteractor, analyticsInteractor);
   }
}
