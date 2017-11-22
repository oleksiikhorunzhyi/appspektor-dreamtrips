package com.worldventures.wallet.domain.storage;

import android.content.Context;

import com.worldventures.core.repository.BaseSnappyRepository;
import com.worldventures.core.repository.SnappyAction;
import com.worldventures.core.repository.SnappyResult;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.wallet.domain.storage.disk.SnappyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class WalletBaseSnappyRepository extends BaseSnappyRepository implements SnappyStorage {

   private final SnappyCrypter snappyCrypter;

   public WalletBaseSnappyRepository(Context context, SnappyCrypter snappyCrypter, ExecutorService executorService) {
      super(context, executorService);
      this.snappyCrypter = snappyCrypter;
   }

   @Override
   public void execute(SnappyAction action) {
      act(action);
   }

   @Override
   public <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      return actWithResult(action);
   }

   void putEncrypted(String key, Object obj) {
      act(db -> snappyCrypter.putEncrypted(db, key, obj));
   }

   <T> T getEncrypted(String key, Class<T> clazz) {
      return actWithResult(db -> snappyCrypter.getEncrypted(db, key, clazz)).orNull();
   }

   <T> List<T> getEncryptedList(String key) {
      return actWithResult(db -> new ArrayList<T>(snappyCrypter.getEncryptedList(db, key))).or(new ArrayList<>());
   }
}
