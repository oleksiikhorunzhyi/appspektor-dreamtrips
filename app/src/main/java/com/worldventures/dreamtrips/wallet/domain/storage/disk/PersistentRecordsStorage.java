package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import java.util.List;

public class PersistentRecordsStorage extends CryptedModelStorage implements RecordsStorage {

   private final String RECORDS_LIST = "WALLET_CARDS_LIST";
   private final String DEFAULT_RECORD_ID = "DEFAULT_WALLET_CARD_ID";
   private final String OFFLINE_MODE_STATE = "OFFLINE_MODE_STATE";

   public PersistentRecordsStorage(SnappyStorage storage, SnappyCrypter snappyCrypter) {
      super(storage, snappyCrypter);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      if (oldVersion == 0) {
         db.del(RECORDS_LIST);
         db.del(DEFAULT_RECORD_ID);
      }
      return true;
   }

   @Override
   public String getKey() {
      return RECORDS_LIST;
   }

   @Override
   public int getVersion() {
      return 1;
   }

   @Override
   public void saveRecords(List<Record> items) {
      putEncrypted(RECORDS_LIST, items);
   }

   @Override
   @NonNull
   public List<Record> readRecords() {
      return getEncryptedList(RECORDS_LIST);
   }

   @Override
   public void deleteAllRecords() {
      execute(db -> db.del(RECORDS_LIST));
   }

   @Override
   public void saveDefaultRecordId(String id) {
      putEncrypted(DEFAULT_RECORD_ID, id);
   }

   @Override
   @Nullable
   public String readDefaultRecordId() {
      return getEncrypted(DEFAULT_RECORD_ID, String.class);
   }

   @Override
   public void deleteDefaultRecordId() {
      execute(db -> db.del(DEFAULT_RECORD_ID));
   }

   @Override
   public void saveOfflineModeState(boolean enabled) {
      put(OFFLINE_MODE_STATE, enabled);
   }

   @Override
   @NonNull
   public Boolean readOfflineModeState() {
      return getOrDefault(OFFLINE_MODE_STATE, Boolean.class, false);
   }

}