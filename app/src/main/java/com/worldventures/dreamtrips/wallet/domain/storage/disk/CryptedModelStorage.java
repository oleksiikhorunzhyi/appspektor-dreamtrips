package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.worldventures.dreamtrips.core.repository.SnappyCrypter;

import java.util.Collections;
import java.util.List;

public abstract class CryptedModelStorage extends ModelStorage {

   protected final SnappyCrypter snappyCrypter;

   public CryptedModelStorage(SnappyCrypter snappyCrypter) {
      this.snappyCrypter = snappyCrypter;
   }

   protected <T> List<T> getEncryptedList(String key) {
      //noinspection all
      return executeWithResult(db -> (List<T>) snappyCrypter.getEncryptedList(db, key)).or(Collections.emptyList());
   }

   protected <T> T getEncrypted(String key, Class<T> clazz) {
      return executeWithResult(db -> snappyCrypter.getEncrypted(db, key, clazz)).orNull();
   }

   protected void putEncrypted(String key, Object obj) {
      execute(db -> snappyCrypter.putEncrypted(db, key, obj));
   }
}
