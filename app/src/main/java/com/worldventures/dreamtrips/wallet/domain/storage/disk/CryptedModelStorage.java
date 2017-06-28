package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.worldventures.dreamtrips.core.repository.SnappyCrypter;

import java.util.Collections;
import java.util.List;

public abstract class CryptedModelStorage extends BaseModelStorage {

   private final SnappyCrypter snappyCrypter;

   public CryptedModelStorage(SnappyStorage storage, SnappyCrypter snappyCrypter) {
      super(storage);
      this.snappyCrypter = snappyCrypter;
   }

   protected void put(String key, Object obj) {
      execute(db -> snappyCrypter.put(db, key, obj));
   }

   protected <T> T get(String key, Class<T> clazz) {
      return executeWithResult(db -> snappyCrypter.get(db, key, clazz)).orNull();
   }

   protected <T> T getOrDefault(String key, Class<T> clazz, T defaultValue) {
      return executeWithResult(db -> snappyCrypter.get(db, key, clazz)).or(defaultValue);
   }

   protected <T> List<T> getList(String key) {
      return executeWithResult(db -> (List<T>) snappyCrypter.getList(db, key)).or(Collections.emptyList());
   }

   protected void putEncrypted(String key, Object obj) {
      execute(db -> snappyCrypter.putEncrypted(db, key, obj));
   }

   protected <T> T getEncrypted(String key, Class<T> clazz) {
      return executeWithResult(db -> snappyCrypter.getEncrypted(db, key, clazz)).orNull();
   }

   protected <T> List<T> getEncryptedList(String key) {
      return executeWithResult(db -> (List<T>) snappyCrypter.getEncryptedList(db, key)).or(Collections.emptyList());
   }

}