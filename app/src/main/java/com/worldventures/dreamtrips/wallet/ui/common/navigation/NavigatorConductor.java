package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import android.content.Context;
import android.net.Uri;

import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningMode;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.model.TransitionModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;

public interface NavigatorConductor {

   void goBack();

   void finish();

   void goWalletSettings();

   void goSettingsProfile();

   void goProvisioningBlocked();

   void goCardList();

   void goAddCard(Record record);

   void goInstallFirmware();

   void goNewFirmwareAvailable();

   void goWizardWelcome(ProvisioningMode provisioningMode);

   void goCardDetails(Record record, TransitionModel transitionModel);

   void goWizardCharging();

   void goFirmwareDownload();

   void goPuckConnection();

   void goStartFirmwareInstall();

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

   void goSettingsDisplayOptions(DisplayOptionsSource source, SmartCardUser user);

   void goExistingCardDetected();

   void goFirmwareUpToDate();

   void goLostCard();

   void goSettingsOfflineMode();

   void goWalletAutoClear();

   void goWalletDisableDefault();

   void goEnterPin();

   void goPaymentFeedBack();

   void goWalletHelpVideo();

   void goWalletCustomerSupport();

   void goWalletHelpDocuments();

   void goSendSmartCardFeedback();

   void goSendCustomerSupportFeedback();

   void goForceUpdatePowerOn();

   void goHelpDocumentDetails(Document document);

   void goPinProposalRecords(String cardNickname);

   void goPreCheckNewCard();

   void goNewCardPowerOn();

   void goForcePairKey();

   void goDialer(Context context, String phoneNumber);

   void goVideoPlayer(Context context, Uri uri, String videoName, Class launchComponent, String videoLanguage);

   void goSendEmail(Context context, Uri uri, String title);

   void goSystemSettings(Context context);

   void goPLayStore(Context context);

   void goSettings(Context context);

}
