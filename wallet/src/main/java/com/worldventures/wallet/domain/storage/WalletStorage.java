package com.worldventures.wallet.domain.storage;

import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardAgreement;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;

public interface WalletStorage {

   SimpleDeviceStorage getWalletDeviceStorage();

   void saveWalletDeviceStorage(SimpleDeviceStorage deviceStorage);

   void saveSmartCard(SmartCard smartCard);

   @Nullable
   SmartCard getSmartCard();

   void deleteSmartCard();

   void saveSmartCardUser(SmartCardUser smartCardUser);

   SmartCardUser getSmartCardUser();

   void deleteSmartCardUser();

   void saveWalletTermsAndConditions(SmartCardAgreement data);

   SmartCardAgreement getWalletTermsAndConditions();

   void deleteTermsAndConditions();

   void saveSmartCardAffidavit(SmartCardAgreement agreement);

   SmartCardAgreement getSmartCardAffidavit();

   void deleteSmartCardAffidavit();

   void saveSmartCardFirmware(SmartCardFirmware smartCardFirmware);

   @Nullable
   SmartCardFirmware getSmartCardFirmware();

   void deleteSmartCardFirmware();

   void saveFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData);

   FirmwareUpdateData getFirmwareUpdateData();

   void deleteFirmwareUpdateData();

   void saveWalletLocations(List<WalletLocation> walletLocations);

   List<WalletLocation> getWalletLocations();

   void deleteWalletLocations();

   void saveSyncRecordsStatus(SyncRecordsStatus data);

   SyncRecordsStatus getSyncRecordsStatus();

   void saveShouldAskForPin(boolean shouldAsk);

   boolean shouldAskForPin();

   void deletePinOptionChoice();

   int getSmartCardDisplayType(int defaultValue);

   void setSmartCardDisplayType(int displayType);

   void deleteSmartCardDisplayType();
}
