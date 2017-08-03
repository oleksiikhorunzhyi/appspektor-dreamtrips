package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import android.net.Uri;

import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.CommonCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.records.SyncAction;

import java.util.List;

public interface Navigator {

   void goBack();

   void finish();

   void goInstallFirmwareWalletStart();

   void goNewFirmwareAvailableWalletStart();

   void goWizardWelcomeWalletStart(ProvisioningMode provisioningMode);

   void goWalletSettings();

   void goSettingsProfile();

   void goProvisioningBlocked();

   void goCardList();

   void goAddCard(RecordViewModel recordViewModel);

   void goInstallFirmware();

   void goNewFirmwareAvailable();

   void goWizardWelcome(ProvisioningMode provisioningMode);

   void goWizardPowerOn();

   void goWizardChecks();

   void goWizardTerms();

   void goWizardSplash();

   void goWizardScanBarcode();

   void goWizardManualInput();

   void goExistingDeviceDetected(String smartCardId);

   void goPairKey(ProvisioningMode provisioningMode, String smartCardId);

   void goPairKeyExistingDevice(ProvisioningMode provisioningMode, String smartCardId);

   void goSyncRecordsPath(SyncAction syncAction);

   void goPinProposalUserSetup(PinProposalAction pinProposalAction);

   void goWizardUploadProfile();

   void goWizardEditProfile();

   void goWizardAssignUser(ProvisioningMode provisioningMode);

   void goEnterPinProposalWizard();

   void goEnterPinProposalRecords();

   void goPinSetSuccess(Action pinSetAction);

   void goWalletPinIsSet();

   void goPaymentSyncFinished();

   void goCardDetails(CommonCardViewModel recordViewModel, TransitionModel transitionModel);

   void goWizardCharging();

   void goFirmwareDownload();

   void goPuckConnection();

   void goStartFirmwareInstall();

   void goStartFirmwareInstallCardList();

   void goWalletFirmwareChecks();

   void goWalletSuccessFirmwareInstall(FirmwareUpdateData firmwareUpdateData);

   void goWalletSuccessFirmwareInstallAfterReset(FirmwareUpdateData firmwareUpdateData);

   void goFactoryReset();

   void goFactoryResetSuccess();

   void goUnassignSuccess();

   void goEnterPinUnassign();

   void goSettingsGeneral();

   void goSettingsSecurity();

   void goSettingsHelp();

   void goSettingsAbout();

   void goSettingsDisplayOptions(DisplayOptionsSource source);

   void goSettingsDisplayOptions(DisplayOptionsSource source, ProfileViewModel profileViewModel);

   void goExistingCardDetected();

   void goFirmwareUpToDate();

   void goLostCard();

   void goSettingsOfflineMode();

   void goWalletAutoClear();

   void goWalletDisableDefault();

   void goEnterPinSettings(Action action);

   void goPaymentFeedBack();

   void goWalletHelpVideo();

   void goWalletCustomerSupport();

   void goWalletHelpDocuments();

   void goSendSmartCardFeedback();

   void goSendCustomerSupportFeedback();

   void goForceUpdatePowerOn();

   void goHelpDocumentDetails(WalletDocument document);

   void goPinProposalRecords(String cardNickname);

   void goPreCheckNewCard();

   void goNewCardPowerOn();

   void goForcePairKey();

   void goFeedBackImageAttachments(int position, List<FeedbackImageAttachment> attachments);

   void goDialer(String phoneNumber);

   void goVideoPlayer(Uri uri, String videoName, Class launchComponent, String videoLanguage);

   void goSendEmail(Uri uri, String title);

   void goSystemSettings();

   void goPlayStore();

   void goSettings();
}
