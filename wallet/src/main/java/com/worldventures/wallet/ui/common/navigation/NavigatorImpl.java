package com.worldventures.wallet.ui.common.navigation;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler;
import com.bluelinelabs.conductor.internal.NoOpControllerChangeHandler;
import com.worldventures.core.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.wallet.ui.dashboard.impl.CardListScreenImpl;
import com.worldventures.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.wallet.ui.provisioning_blocked.impl.WalletProvisioningBlockedScreenImpl;
import com.worldventures.wallet.ui.records.add.RecordBundle;
import com.worldventures.wallet.ui.records.add.impl.AddCardDetailsScreenImpl;
import com.worldventures.wallet.ui.records.detail.impl.CardDetailsEnterAnimHandler;
import com.worldventures.wallet.ui.records.detail.impl.CardDetailsScreenImpl;
import com.worldventures.wallet.ui.records.swiping.impl.WizardChargingScreenImpl;
import com.worldventures.wallet.ui.settings.general.about.impl.AboutScreenImpl;
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSource;
import com.worldventures.wallet.ui.settings.general.firmware.download.impl.WalletDownloadFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.install.impl.WalletInstallFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.installsuccess.impl.WalletSuccessInstallFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.newavailable.impl.WalletNewFirmwareAvailableScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.preinstalletion.impl.WalletFirmwareChecksScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.puck_connection.impl.WalletPuckConnectionScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.pair.impl.ForcePairKeyScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.reset.poweron.impl.ForceUpdatePowerOnScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.start.impl.StartFirmwareInstallScreenImpl;
import com.worldventures.wallet.ui.settings.general.firmware.uptodate.impl.WalletUpToDateFirmwareScreenImpl;
import com.worldventures.wallet.ui.settings.general.impl.WalletGeneralSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.check.impl.PreCheckNewCardScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.detection.impl.ExistingCardDetectScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.pin.impl.EnterPinUnassignScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.poweron.impl.NewCardPowerOnScreenImpl;
import com.worldventures.wallet.ui.settings.general.newcard.success.impl.UnassignSuccessScreenImpl;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.impl.WalletSettingsProfileScreenImpl;
import com.worldventures.wallet.ui.settings.general.reset.impl.FactoryResetScreenImpl;
import com.worldventures.wallet.ui.settings.general.reset.success.impl.FactoryResetSuccessScreenImpl;
import com.worldventures.wallet.ui.settings.help.documents.doc.impl.HelpDocumentDetailScreenImpl;
import com.worldventures.wallet.ui.settings.help.documents.impl.WalletHelpDocumentsScreenImpl;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.wallet.ui.settings.help.feedback.payment.impl.PaymentFeedbackScreenImpl;
import com.worldventures.wallet.ui.settings.help.feedback.regular.FeedbackType;
import com.worldventures.wallet.ui.settings.help.feedback.regular.impl.SendFeedbackScreenImpl;
import com.worldventures.wallet.ui.settings.help.impl.WalletHelpSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.help.support.impl.WalletCustomerSupportSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.help.video.impl.WalletHelpVideoScreenImpl;
import com.worldventures.wallet.ui.settings.impl.WalletSettingsScreenImpl;
import com.worldventures.wallet.ui.settings.security.clear.default_card.impl.WalletDisableDefaultCardScreenImpl;
import com.worldventures.wallet.ui.settings.security.clear.records.impl.WalletAutoClearCardsScreenImpl;
import com.worldventures.wallet.ui.settings.security.impl.WalletSecuritySettingsScreenImpl;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.LostCardScreenImpl;
import com.worldventures.wallet.ui.settings.security.offline_mode.impl.WalletOfflineModeSettingsScreenImpl;
import com.worldventures.wallet.ui.wizard.assign.impl.WizardAssignUserScreenImpl;
import com.worldventures.wallet.ui.wizard.checking.impl.WizardCheckingScreenImpl;
import com.worldventures.wallet.ui.wizard.input.manual.impl.WizardManualInputScreenImpl;
import com.worldventures.wallet.ui.wizard.input.scanner.impl.WizardScanBarcodeScreenImpl;
import com.worldventures.wallet.ui.wizard.pairkey.impl.PairKeyScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.Action;
import com.worldventures.wallet.ui.wizard.pin.complete.impl.WalletPinIsSetScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.enter.impl.EnterPinScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.wallet.ui.wizard.pin.proposal.impl.PinProposalScreenImpl;
import com.worldventures.wallet.ui.wizard.pin.success.impl.PinSetSuccessScreenImpl;
import com.worldventures.wallet.ui.wizard.power_on.impl.WizardPowerOnScreenImpl;
import com.worldventures.wallet.ui.wizard.profile.impl.WizardEditProfileScreenImpl;
import com.worldventures.wallet.ui.wizard.profile.restore.impl.WizardUploadProfileScreenImpl;
import com.worldventures.wallet.ui.wizard.records.SyncAction;
import com.worldventures.wallet.ui.wizard.records.finish.impl.PaymentSyncFinishScreenImpl;
import com.worldventures.wallet.ui.wizard.records.sync.impl.SyncRecordsScreenImpl;
import com.worldventures.wallet.ui.wizard.splash.impl.WizardSplashScreenImpl;
import com.worldventures.wallet.ui.wizard.termsandconditionals.impl.WizardTermsScreenImpl;
import com.worldventures.wallet.ui.wizard.unassign.impl.ExistingDeviceDetectScreenImpl;
import com.worldventures.wallet.ui.wizard.welcome.impl.WizardWelcomeScreenImpl;
import com.worldventures.wallet.util.RevertHorizontalChangeHandler;
import com.worldventures.wallet.util.WalletBuildConfigHelper;

import java.util.ArrayList;
import java.util.List;

import dagger.Lazy;
import timber.log.Timber;

@SuppressWarnings("PMD.GodClass") //TODO: Resolve this PMD error
public class NavigatorImpl implements Navigator {

   private final Lazy<Router> routerLazy;
   private final CoreNavigator coreNavigator;
   private final WalletBuildConfigHelper configHelper;

   public NavigatorImpl(Lazy<Router> routerLazy, CoreNavigator coreNavigator, WalletBuildConfigHelper configHelper) {
      this.routerLazy = routerLazy;
      this.coreNavigator = coreNavigator;
      this.configHelper = configHelper;
   }

   @Override
   public void goBack() {
      routerLazy.get().getActivity().onBackPressed();
   }

   @Override
   public void goBackWithoutHandler() {
      routerLazy.get().popCurrentController();
   }

   @Override
   public void finish() {
      routerLazy.get().getActivity().finish();
   }

   @Override
   public void goInstallFirmwareWalletStart() {
      single(new WalletInstallFirmwareScreenImpl());
   }

   @Override
   public void goNewFirmwareAvailableWalletStart() {
      single(new WalletNewFirmwareAvailableScreenImpl());
   }

   @Override
   public void goWizardWelcomeWalletStart(ProvisioningMode provisioningMode) {
      single(WizardWelcomeScreenImpl.create(provisioningMode));
   }

   @Override
   public void goWalletSettings() {
      go(new WalletSettingsScreenImpl());
   }

   @Override
   public void goSettingsProfile() {
      go(new WalletSettingsProfileScreenImpl());
   }

   @Override
   public void goBackToProfile() {
      final List<RouterTransaction> backstack = new ArrayList<>(
            routerLazy.get().getBackstack().subList(0, routerLazy.get().getBackstackSize() - 2)
      );
      backstack.add(constructTransaction(new WalletSettingsProfileScreenImpl()));
      routerLazy.get().setBackstack(backstack, new HorizontalChangeHandler());
   }

   @Override
   public void goProvisioningBlocked() {
      single(new WalletProvisioningBlockedScreenImpl());
   }

   @Override
   public void goCardList() {
      single(new CardListScreenImpl());
   }

   @Override
   public void goSuccessProvisioningWithRevertAnimation() {
      single(new PaymentSyncFinishScreenImpl(), new RevertHorizontalChangeHandler(), new RevertHorizontalChangeHandler());
   }

   @Override
   public void goAddCard(RecordBundle bundle) {
      withoutLast(AddCardDetailsScreenImpl.create(bundle));
   }

   @Override
   public void goInstallFirmware() {
      go(new WalletInstallFirmwareScreenImpl());
   }

   @Override
   public void goNewFirmwareAvailable() {
      single(new WalletNewFirmwareAvailableScreenImpl());
   }

   @Override
   public void goWizardWelcome(ProvisioningMode provisioningMode) {
      go(WizardWelcomeScreenImpl.create(provisioningMode));
   }

   @Override
   public void goWizardPowerOn() {
      go(new WizardPowerOnScreenImpl());
   }

   @Override
   public void goWizardChecks() {
      single(new WizardCheckingScreenImpl());
   }

   @Override
   public void goWizardTerms() {
      single(new WizardTermsScreenImpl());
   }

   @Override
   public void goWizardSplash() {
      withoutLast(new WizardSplashScreenImpl());
   }

   @Override
   public void goWizardScanBarcode() {
      go(new WizardScanBarcodeScreenImpl());
   }

   @Override
   public void goWizardManualInput() {
      go(new WizardManualInputScreenImpl());
   }

   @Override
   public void goExistingDeviceDetected(String smartCardId) {
      go(ExistingDeviceDetectScreenImpl.create(smartCardId));
   }

   @Override
   public void goPairKey(ProvisioningMode provisioningMode, String smartCardId) {
      go(PairKeyScreenImpl.Companion.create(provisioningMode, smartCardId));
   }

   @Override
   public void goPairKeyExistingDevice(ProvisioningMode provisioningMode, String smartCardId) {
      single(PairKeyScreenImpl.Companion.create(provisioningMode, smartCardId));
   }

   @Override
   public void goSyncRecordsPath(SyncAction syncAction) {
      withoutLast(SyncRecordsScreenImpl.create(syncAction));
   }

   @Override
   public void goPinProposalUserSetup(PinProposalAction pinProposalAction) {
      withoutLast(PinProposalScreenImpl.create(pinProposalAction));
   }

   @Override
   public void goWizardUploadProfile(ProvisioningMode provisioningMode) {
      withoutLast(WizardUploadProfileScreenImpl.create(provisioningMode));
   }

   @Override
   public void goWizardEditProfile(ProvisioningMode provisioningMode) {
      withoutLast(WizardEditProfileScreenImpl.create(provisioningMode));
   }

   @Override
   public void goWizardAssignUser(ProvisioningMode provisioningMode) {
      withoutLast(WizardAssignUserScreenImpl.Companion.create(provisioningMode));
   }

   @Override
   public void goEnterPinProposalWizard() {
      go(EnterPinScreenImpl.create(Action.SETUP));
   }

   @Override
   public void goEnterPinProposalRecords() {
      withoutLast(EnterPinScreenImpl.create(Action.ADD));
   }

   @Override
   public void goPinSetSuccess(Action pinSetAction) {
      withoutLast(PinSetSuccessScreenImpl.create(pinSetAction));
   }

   @Override
   public void goWalletPinIsSet() {
      withoutLast(new WalletPinIsSetScreenImpl());
   }

   @Override
   public void goPaymentSyncFinished() {
      single(new PaymentSyncFinishScreenImpl());
   }

   @Override
   public void goCardDetails(CommonCardViewModel recordViewModel, TransitionModel transitionModel) {
      go(CardDetailsScreenImpl.Companion.create(recordViewModel),
            new CardDetailsEnterAnimHandler(transitionModel), new FadeChangeHandler());
   }

   @Override
   public void goWizardCharging() {
      go(new WizardChargingScreenImpl());
   }

   @Override
   public void goFirmwareDownload() {
      withoutLast(new WalletDownloadFirmwareScreenImpl());
   }

   @Override
   public void goPuckConnection() {
      go(new WalletPuckConnectionScreenImpl());
   }

   @Override
   public void goStartFirmwareInstall() {
      go(new StartFirmwareInstallScreenImpl(), new SimpleSwapChangeHandler(), new SimpleSwapChangeHandler());
   }

   @Override
   public void goStartFirmwareInstallCardList() {
      single(new StartFirmwareInstallScreenImpl(), new SimpleSwapChangeHandler(), new SimpleSwapChangeHandler());
   }

   @Override
   public void goWalletFirmwareChecks() {
      withoutLast(new WalletFirmwareChecksScreenImpl());
   }

   @Override
   public void goWalletSuccessFirmwareInstall(FirmwareUpdateData firmwareUpdateData) {
      single(constructImmediateTransaction(new CardListScreenImpl()));
      go(WalletSuccessInstallFirmwareScreenImpl.create(firmwareUpdateData));
   }

   @Override
   public void goWalletSuccessFirmwareInstallAfterReset(FirmwareUpdateData firmwareUpdateData) {
      single(WalletSuccessInstallFirmwareScreenImpl.create(firmwareUpdateData));
   }

   @Override
   public void goFactoryReset() {
      go(new FactoryResetScreenImpl());
   }

   @Override
   public void goFactoryResetSuccess() {
      single(new FactoryResetSuccessScreenImpl());
   }

   @Override
   public void goUnassignSuccess() {
      single(new UnassignSuccessScreenImpl());
   }

   @Override
   public void goEnterPinUnassign() {
      go(new EnterPinUnassignScreenImpl());
   }

   @Override
   public void goSettingsGeneral() {
      go(new WalletGeneralSettingsScreenImpl());
   }

   @Override
   public void goSettingsSecurity() {
      go(new WalletSecuritySettingsScreenImpl());
   }

   @Override
   public void goSettingsHelp() {
      go(new WalletHelpSettingsScreenImpl());
   }

   @Override
   public void goSettingsAbout() {
      go(new AboutScreenImpl());
   }

   @Override
   public void goSettingsDisplayOptions(DisplayOptionsSource source) {
      go(DisplayOptionsSettingsScreenImpl.Companion.create(source));
   }

   @Override
   public void goSettingsDisplayOptions(DisplayOptionsSource source, ProfileViewModel profileViewModel) {
      go(DisplayOptionsSettingsScreenImpl.Companion.create(profileViewModel, source));
   }

   @Override
   public void goExistingCardDetected() {
      go(new ExistingCardDetectScreenImpl());
   }

   @Override
   public void goFirmwareUpToDate() {
      go(new WalletUpToDateFirmwareScreenImpl());
   }

   @Override
   public void goLostCard() {
      go(new LostCardScreenImpl());
   }

   @Override
   public void goSettingsOfflineMode() {
      go(new WalletOfflineModeSettingsScreenImpl());
   }

   @Override
   public void goWalletAutoClear() {
      go(new WalletAutoClearCardsScreenImpl());
   }

   @Override
   public void goWalletDisableDefault() {
      go(new WalletDisableDefaultCardScreenImpl());
   }

   @Override
   public void goEnterPinSettings(Action action) {
      go(EnterPinScreenImpl.create(action));
   }

   @Override
   public void goPaymentFeedBack() {
      go(new PaymentFeedbackScreenImpl());
   }

   @Override
   public void goWalletHelpVideo() {
      go(new WalletHelpVideoScreenImpl());
   }

   @Override
   public void goWalletCustomerSupport() {
      go(new WalletCustomerSupportSettingsScreenImpl());
   }

   @Override
   public void goWalletHelpDocuments() {
      go(new WalletHelpDocumentsScreenImpl());
   }

   @Override
   public void goForceUpdatePowerOn() {
      single(new ForceUpdatePowerOnScreenImpl());
   }

   @Override
   public void goHelpDocumentDetails(WalletDocumentModel document) {
      go(HelpDocumentDetailScreenImpl.create(document));
   }

   @Override
   public void goPinProposalRecords(String cardNickname) {
      withoutLast(PinProposalScreenImpl.create(PinProposalAction.RECORDS, cardNickname));
   }

   @Override
   public void goPreCheckNewCard() {
      go(new PreCheckNewCardScreenImpl());
   }

   @Override
   public void goNewCardPowerOn() {
      go(new NewCardPowerOnScreenImpl());
   }

   @Override
   public void goForcePairKey() {
      single(new ForcePairKeyScreenImpl());
   }

   @Override
   public void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments) {
      coreNavigator.goFeedBackImageAttachments(position, attachments);
   }

   @Override
   public void goSendCustomerSupportFeedback() {
      go(SendFeedbackScreenImpl.Companion.create(FeedbackType.CustomerSupport));
   }

   @Override
   public void goSendSmartCardFeedback() {
      go(SendFeedbackScreenImpl.Companion.create(FeedbackType.SmartCardFeedback));
   }

   @Override
   public void goDialer(String phoneNumber) {
      Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
      try {
         routerLazy.get().getActivity().startActivity(intent);
      } catch (ActivityNotFoundException e) {
         Timber.e(e);
      }
   }

   @Override
   public void goVideoPlayer(Uri uri, String videoName, Class launchComponent, String videoLanguage) {
      coreNavigator.goVideoPlayer(uri, videoName, launchComponent, videoLanguage);
   }

   @Override
   public void goSendEmail(Uri uri, String title) {
      Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
      routerLazy.get().getActivity().startActivity(Intent.createChooser(emailIntent, title));
   }

   @Override
   public void goSystemSettings() {
      routerLazy.get().getActivity().startActivity(new Intent(Settings.ACTION_SETTINGS));
   }

   @Override
   public void goPlayStore() {
      String appPackageName = configHelper.getAppPackage();
      try {
         routerLazy.get()
               .getActivity()
               .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
      } catch (ActivityNotFoundException exception) {
         routerLazy.get()
               .getActivity()
               .startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
      }
   }

   @Override
   public void goSettings() {
      Intent intent = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      routerLazy.get().getActivity().startActivity(intent);
   }

   private void go(RouterTransaction routerTransaction) {
      routerLazy.get().pushController(routerTransaction);
   }

   private void go(Controller controller) {
      go(constructTransaction(controller));
   }

   private void go(Controller controller, ControllerChangeHandler pushChangeHandler,
         ControllerChangeHandler popChangeHandler) {
      go(constructTransaction(controller, pushChangeHandler, popChangeHandler));
   }

   private void single(RouterTransaction routerTransaction) {
      routerLazy.get().popToRoot(new NoOpControllerChangeHandler());
      routerLazy.get().setRoot(routerTransaction);
   }

   private void single(Controller controller) {
      single(constructTransaction(controller));
   }

   private void single(Controller controller, ControllerChangeHandler pushChangeHandler,
         ControllerChangeHandler popChangeHandler) {
      single(constructTransaction(controller, pushChangeHandler, popChangeHandler));
   }

   private void withoutLast(RouterTransaction routerTransaction) {
      routerLazy.get().replaceTopController(routerTransaction);
   }

   private void withoutLast(Controller controller) {
      withoutLast(constructTransaction(controller));
   }

   private RouterTransaction constructImmediateTransaction(Controller controller) {
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
