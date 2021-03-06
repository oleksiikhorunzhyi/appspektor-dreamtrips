package com.worldventures.wallet.domain.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.core.repository.SnappyAction;
import com.worldventures.core.repository.SnappyResult;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardAgreement;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;

class WalletStorageImpl extends WalletBaseSnappyRepository implements WalletStorage {

   private final static String WALLET_SMART_CARD = "WALLET_SMART_CARD_DATA";
   private final static String WALLET_SMART_CARD_USER = "WALLET_SMART_CARD_USER";
   private final static String WALLET_SMART_CARD_FIRMWARE = "WALLET_SMART_CARD_FIRMWARE";
   private final static String WALLET_DEVICE_STORAGE = "WALLET_DEVICE_STORAGE";
   private final static String WALLET_TERMS_AND_CONDITIONS = "WALLET_TERMS_AND_CONDITIONS";
   private final static String WALLET_AFFIDAVIT = "WALLET_AFFIDAVIT";
   private final static String WALLET_FIRMWARE = "WALLET_FIRMWARE";
   private final static String WALLET_SMART_CARD_LOCATION = "WALLET_SMART_CARD_LOCATION";
   private final static String WALLET_SYNC_RECORD_STATUS = "WALLET_SYNC_RECORD_STATUS";
   private final static String WALLET_OPTIONAL_PIN = "WALLET_OPTIONAL_PIN";
   private final static String WALLET_SMART_CARD_DISPLAY_TYPE = "WALLET_SMART_CARD_DISPLAY_TYPE";

   private final DefaultSnappyOpenHelper defaultSnappyOpenHelper;

   WalletStorageImpl(Context context, SnappyCrypter snappyCrypter, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      super(context, snappyCrypter, defaultSnappyOpenHelper.provideExecutorService());
      this.defaultSnappyOpenHelper = defaultSnappyOpenHelper;
   }

   @Override
   public void execute(SnappyAction action) {
      act(action);
   }

   @Override
   public <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      return actWithResult(action);
   }

   @Override
   public void saveSmartCard(SmartCard smartCard) {
      putEncrypted(WALLET_SMART_CARD, smartCard);
   }

   @Override
   public SmartCard getSmartCard() {
      return getEncrypted(WALLET_SMART_CARD, SmartCard.class);
   }

   @Override
   public void deleteSmartCard() {
      act(db -> db.del(WALLET_SMART_CARD));
   }

   @Override
   public void saveSmartCardUser(SmartCardUser smartCardUser) {
      putEncrypted(WALLET_SMART_CARD_USER, smartCardUser);
   }

   @Override
   public SmartCardUser getSmartCardUser() {
      return getEncrypted(WALLET_SMART_CARD_USER, SmartCardUser.class);
   }

   @Override
   public void deleteSmartCardUser() {
      act(db -> db.del(WALLET_SMART_CARD_USER));
   }

   @Override
   public void saveSmartCardFirmware(SmartCardFirmware smartCardFirmware) {
      putEncrypted(WALLET_SMART_CARD_FIRMWARE, smartCardFirmware);
   }

   @Override
   public SmartCardFirmware getSmartCardFirmware() {
      return getEncrypted(WALLET_SMART_CARD_FIRMWARE, SmartCardFirmware.class);
   }

   @Override
   public void deleteSmartCardFirmware() {
      act(db -> db.del(WALLET_SMART_CARD_FIRMWARE));
   }

   @Override
   public void saveWalletTermsAndConditions(SmartCardAgreement data) {
      putEncrypted(WALLET_TERMS_AND_CONDITIONS, data);
   }

   @Override
   public SmartCardAgreement getWalletTermsAndConditions() {
      return getEncrypted(WALLET_TERMS_AND_CONDITIONS, SmartCardAgreement.class);
   }

   @Override
   public void deleteTermsAndConditions() {
      act(db -> db.del(WALLET_TERMS_AND_CONDITIONS));
   }

   @Override
   public void saveSmartCardAffidavit(SmartCardAgreement agreement) {
      putEncrypted(WALLET_AFFIDAVIT, agreement);

   }

   @Override
   public SmartCardAgreement getSmartCardAffidavit() {
      return getEncrypted(WALLET_AFFIDAVIT, SmartCardAgreement.class);
   }

   @Override
   public void deleteSmartCardAffidavit() {
      act(db -> db.del(WALLET_AFFIDAVIT));
   }

   @Override
   public void saveFirmwareUpdateData(FirmwareUpdateData firmwareUpdateData) {
      putEncrypted(WALLET_FIRMWARE, firmwareUpdateData);
   }

   @Override
   public FirmwareUpdateData getFirmwareUpdateData() {
      return getEncrypted(WALLET_FIRMWARE, FirmwareUpdateData.class);
   }

   @Override
   public void deleteFirmwareUpdateData() {
      act(db -> db.del(WALLET_FIRMWARE));
   }

   @Override
   public void saveWalletDeviceStorage(SimpleDeviceStorage deviceStorage) {
      act(db -> db.put(WALLET_DEVICE_STORAGE, deviceStorage));
   }

   @Override
   public SimpleDeviceStorage getWalletDeviceStorage() {
      return actWithResult(db -> db.get(WALLET_DEVICE_STORAGE, SimpleDeviceStorage.class)).orNull();
   }

   @Override
   public void saveWalletLocations(List<WalletLocation> walletLocations) {
      if (walletLocations == null) {
         walletLocations = new ArrayList<>();
      }
      putEncrypted(WALLET_SMART_CARD_LOCATION, walletLocations);
   }

   @Override
   public List<WalletLocation> getWalletLocations() {
      return getEncryptedList(WALLET_SMART_CARD_LOCATION);
   }

   @Override
   public void deleteWalletLocations() {
      act(db -> db.del(WALLET_SMART_CARD_LOCATION));
   }

   @Override
   public void saveSyncRecordsStatus(SyncRecordsStatus data) {
      act(db -> db.put(WALLET_SYNC_RECORD_STATUS, data));
   }

   @Override
   public SyncRecordsStatus getSyncRecordsStatus() {
      return actWithResult(db -> db.get(WALLET_SYNC_RECORD_STATUS, SyncRecordsStatus.class)).orNull();
   }

   @Override
   public void saveShouldAskForPin(boolean shouldAsk) {
      act(db -> db.putBoolean(WALLET_OPTIONAL_PIN, shouldAsk));
   }

   @Override
   public boolean shouldAskForPin() {
      return actWithResult(db -> db.getBoolean(WALLET_OPTIONAL_PIN)).or(true);
   }

   @Override
   public void deletePinOptionChoice() {
      act(db -> db.del(WALLET_OPTIONAL_PIN));
   }

   @Override
   public int getSmartCardDisplayType(int defaultValue) {
      return actWithResult(db -> db.getInt(WALLET_SMART_CARD_DISPLAY_TYPE)).or(defaultValue);
   }

   @Override
   public void setSmartCardDisplayType(int displayType) {
      act(db -> db.putInt(WALLET_SMART_CARD_DISPLAY_TYPE, displayType));
   }

   @Override
   public void deleteSmartCardDisplayType() {
      act(db -> db.del(WALLET_SMART_CARD_DISPLAY_TYPE));
   }

   @Nullable
   @Override
   protected DB openDbInstance(Context context) throws SnappydbException {
      return defaultSnappyOpenHelper.openDbInstance(context);
   }
}
