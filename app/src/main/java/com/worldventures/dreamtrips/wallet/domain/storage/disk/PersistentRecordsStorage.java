package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

import java.util.List;

public class PersistentRecordsStorage extends CryptedModelStorage {

   private final String RECORDS_LIST = "WALLET_CARDS_LIST";
   private final String DEFAULT_RECORD_ID = "DEFAULT_WALLET_CARD_ID";

   public PersistentRecordsStorage(SnappyStorage storage, SnappyCrypter snappyCrypter) {
      super(storage, snappyCrypter);
   }

   @Override
   public boolean migrate(DB db, int oldVersion) throws SnappydbException {
      return true;
   }

   @Override
   public String getKey() {
      return RECORDS_LIST;
   }

   @Override
   public int getVersion() {
      return 0;
   }

   public void saveRecords(List<Record> items) {
      put(RECORDS_LIST, items);
   }

   public List<Record> readRecords() {
      return getList(RECORDS_LIST);
   }

   public void deleteAllRecords() {
      execute(db -> db.del(RECORDS_LIST));
   }

   public void saveDefaultRecordId(String id) {
      put(DEFAULT_RECORD_ID, id);
   }

   public String readDefaultRecordId() {
      return get(DEFAULT_RECORD_ID, String.class);
   }

   public void deleteDefaultRecordId() {
      execute(db -> db.del(DEFAULT_RECORD_ID));
   }

}