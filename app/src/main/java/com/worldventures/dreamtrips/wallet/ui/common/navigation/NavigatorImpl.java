package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.internal.NoOpControllerChangeHandler;
import com.worldventures.dreamtrips.modules.common.view.activity.PlayerActivity;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.dashboard.impl.CardListScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.add.impl.AddCardDetailsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.detail.impl.CardDetailsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.impl.WizardChargingScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.about.impl.AboutScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailableScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwareScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.impl.WalletGeneralSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl.WalletSettingsProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.impl.FactoryResetScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.FeedbackType;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.impl.SendFeedbackScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.impl.WalletHelpSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.video.impl.WalletHelpVideoScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.impl.WalletSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.disabledefaultcard.impl.WalletDisableDefaultCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.impl.WalletSecuritySettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl.LostCardScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.removecards.impl.WalletAutoClearCardsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.assign.impl.WizardAssignUserScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.checking.impl.WizardCheckingScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.impl.WizardManualInputScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scaner.impl.WizardScanBarcodeScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.impl.PairKeyScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.impl.EnterPinScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.impl.PinProposalScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.success.impl.PinSetSuccessScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.power_on.impl.WizardPowerOnScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.impl.WizardEditProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.restore.impl.WizardUploadProfileScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.sync.impl.SyncRecordsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.impl.WizardSplashScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.impl.WizardTermsScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.impl.WizardWelcomeScreenImpl;

import dagger.Lazy;
import timber.log.Timber;

public class NavigatorImpl implements NavigatorConductor {
   //TODO : double check all "single" routing!

   private final Lazy<Router> routerLazy;

   public NavigatorImpl(Lazy<Router> routerLazy) {
      this.routerLazy = routerLazy;
   }

   @Override
   public void goBack() {
      routerLazy.get().handleBack();
   }

   @Override
   public void finish() {
      routerLazy.get().popToRoot();
   }

   @Override
   public void goWalletSettings() {
      routerLazy.get().pushController(constructTransaction(new WalletSettingsScreenImpl()));
   }

   @Override
   public void goSettingsProfile() {
      routerLazy.get().pushController(constructTransaction(new WalletSettingsProfileScreenImpl()));
   }

   @Override
   public void goProvisioningBlocked() {
      routerLazy.get().replaceTopController(constructTransaction(new WalletProvisioningBlockedScreenImpl()));
   }

   @Override
   public void goCardList() {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructTransaction(new CardListScreenImpl()));
   }

   @Override
   public void goAddCard(Record record) {
      routerLazy.get().pushController(constructTransaction(AddCardDetailsScreenImpl.create(record)));
   }

   @Override
   public void goInstallFirmware() {
      routerLazy.get().pushController(constructTransaction(new WalletInstallFirmwareScreenImpl()));
   }

   @Override
   public void goNewFirmwareAvailable() {
      routerLazy.get().replaceTopController(constructTransaction(new WalletNewFirmwareAvailableScreenImpl()));
   }

   @Override
   public void goWizardWelcome(ProvisioningMode provisioningMode) {
      routerLazy.get().pushController(constructTransaction(WizardWelcomeScreenImpl.create(provisioningMode)));
   }

   @Override
   public void goWizardPowerOn() {
      routerLazy.get().pushController(constructTransaction(new WizardPowerOnScreenImpl()));
   }

   @Override
   public void goWizardChecks() {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructInvisibleTransaction(new WizardCheckingScreenImpl()));
   }

   @Override
   public void goWizardTerms() {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructInvisibleTransaction(new WizardTermsScreenImpl()));
   }

   @Override
   public void goWizardSplash() {
      routerLazy.get().replaceTopController(constructTransaction(new WizardSplashScreenImpl()));
   }

   @Override
   public void goWizardScanBarcode() {
      routerLazy.get().pushController(constructTransaction(new WizardScanBarcodeScreenImpl()));
   }

   @Override
   public void goWizardManualInput() {
      routerLazy.get().pushController(constructTransaction(new WizardManualInputScreenImpl()));
   }

   @Override
   public void goExistingDeviceDetected(String smartCardId) {
      routerLazy.get().pushController(constructTransaction(ExistingDeviceDetectScreenImpl.create(smartCardId)));
   }

   @Override
   public void goPairKey(ProvisioningMode provisioningMode, String smartCardId) {
      routerLazy.get().pushController(constructTransaction(PairKeyScreenImpl.create(provisioningMode, smartCardId)));
   }

   @Override
   public void goPairKeyExistingDevice(ProvisioningMode provisioningMode, String smartCardId) {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      goPairKey(provisioningMode, smartCardId);
   }

   @Override
   public void goSyncRecordsPath(SyncAction syncAction) {
      routerLazy.get().replaceTopController(constructTransaction(SyncRecordsScreenImpl.create(syncAction)));
   }

   @Override
   public void goPinProposalUserSetup(PinProposalAction pinProposalAction) {
      routerLazy.get().replaceTopController(constructTransaction(PinProposalScreenImpl.create(pinProposalAction)));
   }

   @Override
   public void goWizardUploadProfile() {
      routerLazy.get().replaceTopController(constructTransaction(new WizardUploadProfileScreenImpl()));
   }

   @Override
   public void goWizardEditProfile() {
      routerLazy.get().replaceTopController(constructTransaction(new WizardEditProfileScreenImpl()));
   }

   @Override
   public void goWizardAssignUser(ProvisioningMode provisioningMode) {
      routerLazy.get().replaceTopController(constructTransaction(WizardAssignUserScreenImpl.create(provisioningMode)));
   }

   @Override
   public void goEnterPinProposal(Action pinAction) {
      routerLazy.get().replaceTopController(constructTransaction(EnterPinScreenImpl.create(pinAction)));
   }

   @Override
   public void goPinSetSuccess(Action pinSetAction) {
      routerLazy.get().replaceTopController(constructTransaction(PinSetSuccessScreenImpl.create(pinSetAction)));
   }

   @Override
   public void goWalletPinIsSet() {
      routerLazy.get().replaceTopController(constructTransaction(new WalletPinIsSetScreenImpl()));
   }

   @Override
   public void goPaymentSyncFinished() {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructInvisibleTransaction(new PaymentSyncFinishScreenImpl()));
   }

   @Override
   public void goCardDetails(Record record, TransitionModel transitionModel) {
      routerLazy.get().pushController(constructTransaction(CardDetailsScreenImpl.create(record, transitionModel)));
   }

   @Override
   public void goWizardCharging() {
      routerLazy.get().pushController(constructTransaction(new WizardChargingScreenImpl()));
   }

   @Override
   public void goFirmwareDownload() {
      routerLazy.get().replaceTopController(constructTransaction(new WalletDownloadFirmwareScreenImpl()));
   }

   @Override
   public void goPuckConnection() {
      routerLazy.get().pushController(constructTransaction(new WalletPuckConnectionScreenImpl()));
   }

   @Override
   public void goStartFirmwareInstall() {
      routerLazy.get().replaceTopController(constructTransaction(new StartFirmwareInstallScreenImpl()));
   }

   @Override
   public void goWalletFirmwareChecks() {
      routerLazy.get().replaceTopController(constructTransaction(new WalletFirmwareChecksScreenImpl()));
   }

   @Override
   public void goWalletSuccessFirmwareInstall(FirmwareUpdateData firmwareUpdateData) {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructInvisibleTransaction(new CardListScreenImpl()));
      routerLazy.get().pushController(constructTransaction(WalletSuccessInstallFirmwareScreenImpl.create(firmwareUpdateData)));
   }

   @Override
   public void goWalletSuccessFirmwareInstallAfterReset(FirmwareUpdateData firmwareUpdateData) {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructTransaction(WalletSuccessInstallFirmwareScreenImpl.create(firmwareUpdateData)));
   }

   @Override
   public void goFactoryReset() {
      routerLazy.get().pushController(constructTransaction(new FactoryResetScreenImpl()));
   }

   @Override
   public void goFactoryResetSuccess() {
      routerLazy.get().replaceTopController(constructTransaction(new FactoryResetSuccessScreenImpl()));
   }

   @Override
   public void goUnassignSuccess() {
      routerLazy.get().replaceTopController(constructTransaction(new UnassignSuccessScreenImpl()));
   }

   @Override
   public void goEnterPinUnassign() {
      routerLazy.get().pushController(constructTransaction(new EnterPinUnassignScreenImpl()));
   }

   @Override
   public void goSettingsGeneral() {
      routerLazy.get().pushController(constructTransaction(new WalletGeneralSettingsScreenImpl()));
   }

   @Override
   public void goSettingsSecurity() {
      routerLazy.get().pushController(constructTransaction(new WalletSecuritySettingsScreenImpl()));
   }

   @Override
   public void goSettingsHelp() {
      routerLazy.get().pushController(constructTransaction(new WalletHelpSettingsScreenImpl()));
   }

   @Override
   public void goSettingsAbout() {
      routerLazy.get().pushController(constructTransaction(new AboutScreenImpl()));
   }

   @Override
   public void goSettingsDisplayOptions(DisplayOptionsSource source) {
      routerLazy.get().pushController(constructTransaction(DisplayOptionsSettingsScreenImpl.create(source)));
   }

   @Override
   public void goSettingsDisplayOptions(DisplayOptionsSource source, SmartCardUser user) {
      routerLazy.get().pushController(constructTransaction(DisplayOptionsSettingsScreenImpl.create(user, source)));
   }

   @Override
   public void goExistingCardDetected() {
      routerLazy.get().pushController(constructTransaction(new ExistingCardDetectScreenImpl()));
   }

   @Override
   public void goFirmwareUpToDate() {
      routerLazy.get().pushController(constructTransaction(new WalletUpToDateFirmwareScreenImpl()));
   }

   @Override
   public void goLostCard() {
      routerLazy.get().pushController(constructTransaction(new LostCardScreenImpl()));
   }

   @Override
   public void goSettingsOfflineMode() {
      routerLazy.get().pushController(constructTransaction(new WalletOfflineModeSettingsScreenImpl()));
   }

   @Override
   public void goWalletAutoClear() {
      routerLazy.get().pushController(constructTransaction(new WalletAutoClearCardsScreenImpl()));
   }

   @Override
   public void goWalletDisableDefault() {
      routerLazy.get().pushController(constructTransaction(new WalletDisableDefaultCardScreenImpl()));
   }

   @Override
   public void goEnterPinSettings(Action action) {
      routerLazy.get().pushController(constructTransaction(EnterPinScreenImpl.create(action)));
   }

   @Override
   public void goPaymentFeedBack() {
      routerLazy.get().pushController(constructTransaction(new PaymentFeedbackScreenImpl()));
   }

   @Override
   public void goWalletHelpVideo() {
      routerLazy.get().pushController(constructTransaction(new WalletHelpVideoScreenImpl()));
   }

   @Override
   public void goWalletCustomerSupport() {
      routerLazy.get().pushController(constructTransaction(new WalletCustomerSupportSettingsScreenImpl()));
   }

   @Override
   public void goWalletHelpDocuments() {
      routerLazy.get().pushController(constructTransaction(new WalletHelpDocumentsScreenImpl()));
   }

   @Override
   public void goForceUpdatePowerOn() {
      routerLazy.get().replaceTopController(constructTransaction(new ForceUpdatePowerOnScreenImpl()));
   }

   @Override
   public void goHelpDocumentDetails(Document document) {
      routerLazy.get().replaceTopController(constructTransaction(HelpDocumentDetailScreenImpl.create(document)));
   }

   @Override
   public void goPinProposalRecords(String cardNickname) {
      routerLazy.get().replaceTopController(constructTransaction(PinProposalScreenImpl.create(PinProposalAction.RECORDS, cardNickname)));
   }

   @Override
   public void goPreCheckNewCard() {
      routerLazy.get().pushController(constructTransaction(new PreCheckNewCardScreenImpl()));
   }

   @Override
   public void goNewCardPowerOn() {
      routerLazy.get().pushController(constructTransaction(new NewCardPowerOnScreenImpl()));
   }

   @Override
   public void goForcePairKey() {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().pushController(constructTransaction(new ForcePairKeyScreenImpl()));
   }

   @Override
   public void goSendCustomerSupportFeedback() {
      routerLazy.get().pushController(constructTransaction(SendFeedbackScreenImpl.create(FeedbackType.CustomerSupport)));
   }

   @Override
   public void goSendSmartCardFeedback() {
      routerLazy.get().pushController(constructTransaction(SendFeedbackScreenImpl.create(FeedbackType.SmartCardFeedback)));
   }

   @Override
   public void goDialer(Context context, String phoneNumber) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
      try {
         context.startActivity(intent);
      } catch (ActivityNotFoundException e) {
         Timber.e(e, "");
      }
   }

   @Override
   public void goVideoPlayer(Context context, Uri uri, String videoName, Class launchComponent, String videoLanguage) {
      Intent intent = new Intent(context, PlayerActivity.class).setData(uri)
            .putExtra(PlayerActivity.EXTRA_VIDEO_NAME, videoName)
            .putExtra(PlayerActivity.EXTRA_LAUNCH_COMPONENT, getClass())
            .putExtra(PlayerActivity.EXTRA_LANGUAGE, videoLanguage);
      context.startActivity(intent);
   }

   @Override
   public void goSendEmail(Context context, Uri uri, String title) {
      Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
      context.startActivity(Intent.createChooser(emailIntent, title));
   }

   @Override
   public void goSystemSettings(Context context) {
      context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
   }

   public void goPLayStore(Context context) {
      String appPackageName = "com.worldventures.dreamtrips";
      try {
         context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
      } catch (android.content.ActivityNotFoundException exception) {
         context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
      }
   }

   public void goSettings(Context context) {
      Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
   }

   private RouterTransaction constructInvisibleTransaction(Controller controller) {
      //TODO : ensure this makes transactions instant
      return constructTransaction(controller, new NoOpControllerChangeHandler(), new NoOpControllerChangeHandler());
   }

   private RouterTransaction constructTransaction(Controller controller) {
      return constructTransaction(controller, new HorizontalChangeHandler(), new HorizontalChangeHandler());
   }

   private RouterTransaction constructTransaction(Controller controller, ControllerChangeHandler pushChangeHandler,
         ControllerChangeHandler popChangeHandler) {
      return RouterTransaction.with(controller)
            .pushChangeHandler(pushChangeHandler)
            .popChangeHandler(popChangeHandler);
   }
}
