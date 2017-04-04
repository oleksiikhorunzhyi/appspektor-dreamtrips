package com.worldventures.dreamtrips.wallet.domain.storage;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.List;
import java.util.Map;

import io.techery.janet.smartcard.action.settings.SetStatusBarSettingAction;
import io.techery.janet.smartcard.mock.device.DeviceStorage;
import io.techery.janet.smartcard.mock.device.SimpleDeviceStorage;
import io.techery.janet.smartcard.model.FirmwareVersion;
import io.techery.janet.smartcard.model.Record;
import io.techery.janet.smartcard.model.User;

public class PersistentDeviceStorage implements DeviceStorage {

   private final SnappyRepository db;
   private final SimpleDeviceStorage memoryStorage;

   public static PersistentDeviceStorage load(SnappyRepository db) {
      SimpleDeviceStorage memoryDeviceStorage = db.getWalletDeviceStorage();
      if (memoryDeviceStorage == null) {
         memoryDeviceStorage = new SimpleDeviceStorage();
      }
      return new PersistentDeviceStorage(db, memoryDeviceStorage);
   }

   private PersistentDeviceStorage(SnappyRepository db, SimpleDeviceStorage memoryStorage) {
      this.db = db;
      this.memoryStorage = memoryStorage;
   }

   private void persistStorage() {
      db.saveWalletDeviceStorage(memoryStorage);
   }

   @Override
   public FirmwareVersion getFirmwareVersion() {
      return memoryStorage.getFirmwareVersion();
   }

   @Override
   public void setFirmwareVersion(FirmwareVersion firmwareVersion) {
      memoryStorage.setFirmwareVersion(firmwareVersion);
      persistStorage();
   }

   @Override
   public String getBatteryLevel() {
      return memoryStorage.getBatteryLevel();
   }

   @Override
   public void setBatteryLevel(String batteryLevel) {
      memoryStorage.setBatteryLevel(batteryLevel);
      persistStorage();
   }

   @Override
   public void deletePairMetadata(String key) {
      memoryStorage.deletePairMetadata(key);
      persistStorage();
   }

   @Override
   public void setPairMetadata(String key, String value) {
      memoryStorage.setPairMetadata(key, value);
      persistStorage();
   }

   @Override
   public void lock(boolean lock) {
      memoryStorage.lock(lock);
      persistStorage();
   }

   @Override
   public boolean isLocked() {
      return memoryStorage.isLocked();
   }

   @Override
   public void setPowerSavingModeEnable(boolean powerSavingModeEnable) {
      memoryStorage.setPowerSavingModeEnable(powerSavingModeEnable);
      persistStorage();
   }

   @Override
   public boolean isPowerSavingModeEnabled() {
      return memoryStorage.isPowerSavingModeEnabled();
   }

   @Override
   public boolean getStealthModeStatus() {
      return memoryStorage.getStealthModeStatus();
   }

   @Override
   public void setStealthModeStatus(boolean enable) {
      memoryStorage.setStealthModeStatus(enable);
      persistStorage();
   }

   @Override
   public void setStatusBarSettings(SetStatusBarSettingAction.IconType iconType, boolean enable) {
      memoryStorage.setStatusBarSettings(iconType, enable);
      persistStorage();
   }

   @Override
   public Map<SetStatusBarSettingAction.IconType, Boolean> getStatusBarSettings() {
      return memoryStorage.getStatusBarSettings();
   }

   @Override
   public void setPhotoVisible(boolean photoVisible) {
      memoryStorage.setPhotoVisible(photoVisible);
      persistStorage();
   }

   @Override
   public boolean isPhotoVisible() {
      return memoryStorage.isPhotoVisible();
   }

   @Override
   public int getBeaconSleepingRxPeriod() {
      return memoryStorage.getBeaconSleepingRxPeriod();
   }

   @Override
   public void setBeaconSleepingRxPeriod(int period) {
      memoryStorage.setBeaconSleepingRxPeriod(period);
      persistStorage();
   }

   @Override
   public int getBeaconSleepingTxPeriod() {
      return memoryStorage.getBeaconSleepingTxPeriod();
   }

   @Override
   public void setBeaconSleepingTxPeriod(int period) {
      memoryStorage.setBeaconSleepingTxPeriod(period);
      persistStorage();
   }

   @Override
   public long getDisableDefaultCardDelay() {
      return memoryStorage.getDisableDefaultCardDelay();
   }

   @Override
   public void setDisableDefaultCardDelay(long delay) {
      memoryStorage.setDisableDefaultCardDelay(delay);
      persistStorage();
   }

   @Override
   public List<Record> getMemberRecords() {
      return memoryStorage.getMemberRecords();
   }

   @Override
   public int addRecord(Record record) {
      int recordId = memoryStorage.addRecord(record);
      persistStorage();
      return recordId;
   }

   @Override
   public void deleteRecord(int recordId) {
      memoryStorage.deleteRecord(recordId);
      persistStorage();
   }

   @Override
   public void deleteRecordMetadata(int recordId, String key) {
      memoryStorage.deleteRecordMetadata(recordId, key);
      persistStorage();
   }

   @Override
   public void editRecord(Record record) {
      memoryStorage.editRecord(record);
      persistStorage();
   }

   @Override
   public void editRecordMetadata(int recordId, String key, String value) {
      memoryStorage.editRecordMetadata(recordId, key, value);
      persistStorage();
   }

   @Override
   public void setDefaultRecord(int recordId) {
      memoryStorage.setDefaultRecord(recordId);
      persistStorage();
   }

   @Override
   public int getDefaultRecord() {
      return memoryStorage.getDefaultRecord();
   }

   @Override
   public void setActiveRecord(int recordId) {
      memoryStorage.setActiveRecord(recordId);
      persistStorage();
   }

   @Override
   public int getActiveRecord() {
      return memoryStorage.getActiveRecord();
   }

   @Override
   public void setClearRecordsDelay(long delay) {
      memoryStorage.setClearRecordsDelay(delay);
      persistStorage();
   }

   @Override
   public long getClearRecordsDelay() {
      return memoryStorage.getClearRecordsDelay();
   }

   @Override
   public User getUser() {
      return memoryStorage.getUser();
   }

   @Override
   public void assignUser(User user) {
      memoryStorage.assignUser(user);
      persistStorage();
   }

   @Override
   public void updateUser(User user) {
      memoryStorage.updateUser(user);
      persistStorage();
   }

   @Override
   public void unAssignUser() {
      memoryStorage.unAssignUser();
      persistStorage();
   }

   @Override
   public boolean isUserAssigned() {
      return memoryStorage.isUserAssigned();
   }

   @Override
   public void updateUserPhoto(byte[] bytes) {
      memoryStorage.updateUserPhoto(bytes);
      persistStorage();
   }

   @Override
   public boolean isCardRecording() {
      return memoryStorage.isCardRecording();
   }

   @Override
   public void setCardRecording(boolean recording) {
      memoryStorage.setCardRecording(recording);
      persistStorage();
   }

   @Override
   public void enableLockUnlockCard(boolean enable) {
      memoryStorage.enableLockUnlockCard(enable);
      persistStorage();
   }

   @Override
   public boolean isLockUnlockCardEnabled() {
      return memoryStorage.isLockUnlockCardEnabled();
   }

   @Override
   public void setPinEnabled(boolean enabled) {
      memoryStorage.setPinEnabled(enabled);
      persistStorage();
   }

   @Override
   public boolean isPinEnabled() {
      return memoryStorage.isPinEnabled();
   }

   @Override
   public void setCardTime(long time) {
      memoryStorage.setCardTime(time);
      persistStorage();
   }

   @Override
   public long getCardTime() {
      return memoryStorage.getCardTime();
   }
}
