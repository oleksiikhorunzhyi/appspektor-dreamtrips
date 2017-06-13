package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.snappydb.DB;
import com.snappydb.SnappydbException;

import java.util.Collection;

public class SnappyStorageManager {

   private final Collection<ModelStorage> modelStorageList;

   public SnappyStorageManager(Collection<ModelStorage> modelStorageList) {
      this.modelStorageList = modelStorageList;
   }

   public void init() {
      for (ModelStorage storage : modelStorageList) {
         storage.execute(db -> checkVersion(db, storage));
      }
   }

   private void checkVersion(DB db, ModelStorage storage) throws SnappydbException {
      final String versionKey = getVersionKey(storage);

      if (!db.exists(storage.getKey())) {
         db.put(versionKey, storage.getVersion());
         return;
      }

      final int storedVersion = db.exists(versionKey) ? db.getInt(versionKey) : 0;
      if (storedVersion < storage.getVersion()) {
         if (storage.migrate(db, storedVersion)) {
            db.put(versionKey, storage.getVersion());
         }
      }
   }

   private String getVersionKey(ModelStorage storage) {
      return "version_" + storage.getKey();
   }

}