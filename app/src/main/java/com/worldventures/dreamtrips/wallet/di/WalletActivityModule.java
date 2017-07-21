package com.worldventures.dreamtrips.wallet.di;

import android.app.Activity;

import com.bluelinelabs.conductor.Router;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.session.UserSession;
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
import com.worldventures.dreamtrips.wallet.service.WalletDetectLocationService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.FlowNavigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
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
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.WizardCheckingPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scaner.WizardScanBarcodePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.WalletPinIsSetPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.PinSetSuccessPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.WizardPowerOnPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.WizardUploadProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.PaymentSyncFinishPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.SyncRecordsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePresenter;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

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
            WizardSplashPresenter.class,
            WizardPowerOnPresenter.class,
            WizardTermsPresenter.class,
            WizardScanBarcodePresenter.class,
            WizardManualInputPresenter.class,
            WizardEditProfilePresenter.class,
            WalletPinIsSetPresenter.class,
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
            PinSetSuccessPresenter.class,
            AddCardDetailsScreenImpl.class,
            EnterPinPresenter.class,
            WizardWelcomePresenter.class,
            WalletAutoClearCardsScreenImpl.class,
            WalletDisableDefaultCardScreenImpl.class,
            WalletUpToDateFirmwareScreenImpl.class,
            WalletSuccessInstallFirmwareScreenImpl.class,
            WalletFirmwareChecksScreenImpl.class,
            WalletDownloadFirmwareScreenImpl.class,
            FactoryResetScreenImpl.class,
            FactoryResetSuccessScreenImpl.class,
            WalletInstallFirmwareScreenImpl.class,
            WizardCheckingPresenter.class,
            WalletNewFirmwareAvailableScreenImpl.class,
            WalletPuckConnectionScreenImpl.class,
            WizardAssignUserPresenter.class,
            AboutScreenImpl.class,
            PairKeyPresenter.class,
            ConnectionErrorScreenImpl.class,
            StartFirmwareInstallScreenImpl.class,
            ForceUpdatePowerOnScreenImpl.class,
            ForcePairKeyScreenImpl.class,
            LostCardScreenImpl.class,
            ExistingCardDetectScreenImpl.class,
            UnassignSuccessScreenImpl.class,
            EnterPinUnassignScreenImpl.class,
            SyncRecordsPresenter.class,
            NewCardPowerOnScreenImpl.class,
            PreCheckNewCardScreenImpl.class,
            PaymentSyncFinishPresenter.class,
            ExistingDeviceDetectPresenter.class,
            WizardUploadProfilePresenter.class,
            WalletHelpDocumentsScreenImpl.class,
            SendFeedbackScreenImpl.class,
            WalletHelpVideoScreenImpl.class,
            HelpDocumentDetailScreenImpl.class,
            PinProposalPresenter.class,
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
   Navigator provideNavigator(Activity activity) {
      return new FlowNavigator(activity);
   }

   @Singleton
   @Provides
   Router provideRouter(Activity activity) {
      return ((WalletActivity) activity).getRouter();
   }

   @Singleton
   @Provides
   NavigatorConductor provideConductorNavigator(Lazy<Router> router) {
      return new NavigatorImpl(router);
   }

   @Provides
   @Singleton
   WalletCropImageService provideWalletCropImageDelegate(Activity activity) {
      return new WalletCropImageServiceImpl(activity);
   }

   @Provides
   WalletStartPresenter provideWalletStartPresenter(NavigatorConductor navigatorConductor, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor, WalletAccessValidator walletAccessValidator,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletStartPresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor,
            walletAccessValidator, httpErrorHandlingUtil);
   }

   @Provides
   WalletProvisioningBlockedPresenter provideWalletProvisioningBlockedPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletProvisioningBlockedPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   CardListPresenter provideCardListPresenter(NavigatorConductor navigatorConductor, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, RecordInteractor recordInteractor, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory,
         FactoryResetInteractor factoryResetInteractor, NavigationDrawerPresenter navigationDrawerPresenter,
         WalletFeatureHelper walletFeatureHelper) {
      return new CardListPresenterImpl(navigatorConductor, smartCardInteractor, networkService, recordInteractor,
            firmwareInteractor, analyticsInteractor, errorHandlerFactory, factoryResetInteractor,
            navigationDrawerPresenter, walletFeatureHelper);
   }

   @Provides
   WalletSettingsPresenter provideWalletSettingsPresenter(NavigatorConductor navigatorConductor, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   WalletGeneralSettingsPresenter providesWalletGeneralSettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         FactoryResetInteractor factoryResetInteractor, AnalyticsInteractor analyticsInteractor, WalletFeatureHelper walletFeatureHelper) {
      return new WalletGeneralSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor,
            factoryResetInteractor, analyticsInteractor, walletFeatureHelper);
   }

   @Provides
   FactoryResetPresenter provideFactoryResetPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new FactoryResetPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor, factoryResetInteractor);
   }

   @Provides
   FactoryResetSuccessPresenter provideFactoryResetSuccessPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new FactoryResetSuccessPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   NewCardPowerOnPresenter provideNewCardPowerOnPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, WalletBluetoothService walletBluetoothService) {
      return new NewCardPowerOnPresenterImpl(navigatorConductor, smartCardInteractor, networkService, factoryResetInteractor,
            analyticsInteractor, walletBluetoothService);
   }

   @Provides
   PreCheckNewCardPresenter providePreCheckNewCardPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor, WalletBluetoothService walletBluetoothService) {
      return new PreCheckNewCardPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor,
            factoryResetInteractor, walletBluetoothService);
   }

   @Provides
   ExistingCardDetectPresenter provideExistingCardDetectPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new ExistingCardDetectPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor,
            factoryResetInteractor);
   }

   @Provides
   EnterPinUnassignPresenter provideEnterPinUnassignPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FactoryResetInteractor factoryResetInteractor) {
      return new EnterPinUnassignPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            factoryResetInteractor, analyticsInteractor);
   }

   @Provides
   UnassignSuccessPresenter provideUnassignSuccessPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new UnassignSuccessPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor);
   }

   @Provides
   AboutPresenter provideAboutPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor) {
      return new AboutPresenterImpl(navigatorConductor, smartCardInteractor, networkService, recordInteractor, analyticsInteractor);
   }

   @Provides
   WalletSecuritySettingsPresenter provideWalletSecuritySettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory, WalletFeatureHelper walletFeatureHelper) {
      return new WalletSecuritySettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory, walletFeatureHelper);
   }

   @Provides
   WalletHelpSettingsPresenter provideWalletHelpSettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new WalletHelpSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService);
   }

   @Provides
   WalletSettingsProfilePresenter provideWalletSettingsProfilePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, SmartCardUserDataInteractor smartCardUserDataInteractor,
         SessionHolder<UserSession> sessionHolder) {
      return new WalletSettingsProfilePresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor, smartCardUserDataInteractor, sessionHolder);
   }

   @Provides
   WalletCustomerSupportSettingsPresenter provideWalletCustomerSupportSettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         WalletSettingsInteractor walletSettingsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletCustomerSupportSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            walletSettingsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   LostCardPresenter provideLostCardPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         PermissionDispatcher permissionDispatcher, SmartCardLocationInteractor smartCardLocationInteractor,
         WalletDetectLocationService walletDetectLocationService, AnalyticsInteractor analyticsInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new LostCardPresenterImpl(navigatorConductor, smartCardInteractor, networkService, permissionDispatcher,
            smartCardLocationInteractor, walletDetectLocationService, analyticsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WalletAutoClearCardsPresenter provideWalletAutoClearCardsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletAutoClearCardsPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletDisableDefaultCardPresenter provideWalletDisableDefaultCardPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletDisableDefaultCardPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletOfflineModeSettingsPresenter provideWalletOfflineModeSettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletOfflineModeSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   DisplayOptionsSettingsPresenter provideDisplayOptionsSettingsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new DisplayOptionsSettingsPresenterImpl(navigatorConductor, smartCardInteractor, networkService);
   }

   @Provides
   WalletNewFirmwareAvailablePresenter provideWalletNewFirmwareAvailablePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletNewFirmwareAvailablePresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   WalletPuckConnectionPresenter provideWalletPuckConnectionPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new WalletPuckConnectionPresenterImpl(navigatorConductor, smartCardInteractor, networkService);
   }

   @Provides
   WalletDownloadFirmwarePresenter provideWalletDownloadFirmwarePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         FirmwareInteractor firmwareInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletDownloadFirmwarePresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor,
            firmwareInteractor, errorHandlerFactory);
   }

   @Provides
   WalletFirmwareChecksPresenter provideWalletFirmwareChecksPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WalletBluetoothService walletBluetoothService,
         FirmwareInteractor firmwareInteractor, AnalyticsInteractor analyticsInteractor) {
      return new WalletFirmwareChecksPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            walletBluetoothService, firmwareInteractor, analyticsInteractor);
   }

   @Provides
   WalletInstallFirmwarePresenter provideWalletInstallFirmwarePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WalletInstallFirmwarePresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   WalletSuccessInstallFirmwarePresenter provideWalletSuccessInstallFirmwarePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor) {
      return new WalletSuccessInstallFirmwarePresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor);
   }

   @Provides
   ForceUpdatePowerOnPresenter provideForceUpdatePowerOnPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, WizardInteractor wizardInteractor,
         WalletBluetoothService walletBluetoothService) {
      return new ForceUpdatePowerOnPresenterImpl(navigatorConductor, smartCardInteractor, networkService, wizardInteractor,
            walletBluetoothService);
   }

   @Provides
   ForcePairKeyPresenter provideForcePairKeyPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, FirmwareInteractor firmwareInteractor,
         ErrorHandlerFactory errorHandlerFactory) {
      return new ForcePairKeyPresenterImpl(navigatorConductor, smartCardInteractor, networkService, firmwareInteractor, errorHandlerFactory);
   }

   @Provides
   StartFirmwareInstallPresenter provideStartFirmwareInstallPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         ErrorHandlerFactory errorHandlerFactory, FirmwareInteractor firmwareInteractor) {
      return new StartFirmwareInstallPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            errorHandlerFactory, firmwareInteractor);
   }

   @Provides
   WalletUpToDateFirmwarePresenter providesWalletUpToDateFirmwarePresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor) {
      return new WalletUpToDateFirmwarePresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            analyticsInteractor);
   }

   @Provides
   WalletHelpVideoPresenter provideWalletHelpVideoPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, MemberVideosInteractor memberVideosInteractor,
         CachedEntityInteractor cachedEntityInteractor, CachedEntityDelegate cachedEntityDelegate, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletHelpVideoPresenterImpl(navigatorConductor, smartCardInteractor, networkService, memberVideosInteractor,
            cachedEntityInteractor, cachedEntityDelegate, httpErrorHandlingUtil);
   }

   @Provides
   WalletHelpDocumentsPresenter provideWalletHelpDocumentsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         DocumentsInteractor documentsInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new WalletHelpDocumentsPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            documentsInteractor, httpErrorHandlingUtil);
   }

   @Provides
   HelpDocumentDetailPresenter provideHelpDocumentDetailPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new HelpDocumentDetailPresenterImpl(navigatorConductor, smartCardInteractor, networkService);
   }

   @Provides
   SendFeedbackPresenter providesSendFeedbackPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaInteractor mediaInteractor, com.worldventures.dreamtrips.core.navigation.router.Router router) {
      return new SendFeedbackPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            feedbackInteractor, walletSettingsInteractor, mediaInteractor, router);
   }

   @Provides
   PaymentFeedbackPresenter providePaymentFeedbackPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         FeedbackInteractor feedbackInteractor, WalletSettingsInteractor walletSettingsInteractor,
         MediaInteractor mediaInteractor, com.worldventures.dreamtrips.core.navigation.router.Router router) {
      return new PaymentFeedbackPresenterImpl(navigatorConductor, smartCardInteractor, networkService,
            feedbackInteractor, walletSettingsInteractor, mediaInteractor, router);
   }

   @Provides
   WizardChargingPresenter providesWizardChargingPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService, RecordInteractor recordInteractor,
         AnalyticsInteractor analyticsInteractor, ErrorHandlerFactory errorHandlerFactory) {
      return new WizardChargingPresenterImpl(navigatorConductor, smartCardInteractor, networkService, recordInteractor,
            analyticsInteractor, errorHandlerFactory);
   }

   @Provides
   ConnectionErrorPresenter provideConnectionErrorPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService) {
      return new ConnectionErrorPresenterImpl(navigatorConductor, smartCardInteractor, networkService);
   }

   @Provides
   AddCardDetailsPresenter provideAddCardDetailsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor, WizardInteractor wizardInteractor,
         HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new AddCardDetailsPresenterImpl(navigatorConductor, smartCardInteractor, networkService, analyticsInteractor,
            recordInteractor, wizardInteractor, httpErrorHandlingUtil);
   }

   @Provides
   CardDetailsPresenter provideCardDetailsPresenter(NavigatorConductor navigatorConductor,
         SmartCardInteractor smartCardInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor, RecordInteractor recordInteractor, HttpErrorHandlingUtil httpErrorHandlingUtil) {
      return new CardDetailsPresenterImpl(navigatorConductor, smartCardInteractor, networkService, recordInteractor,
            analyticsInteractor, httpErrorHandlingUtil);
   }
}
