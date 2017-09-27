package com.worldventures.dreamtrips.wallet.domain.storage.persistent;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyResult;
import com.worldventures.dreamtrips.wallet.domain.storage.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletBaseSnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorage;

import java.util.concurrent.ExecutorService;

/**
 * Repository is not cleared on logout.
 */
public class PersistentSnappyRepositoryImpl implements SnappyStorage {

   private final SnappyStorage internalSnappyRepository;

   private final PersistentStorageNameProvider storageNameProvider;

   @Nullable
   private String persistentStorageFolderName;

   public PersistentSnappyRepositoryImpl(Context context, SnappyCrypter snappyCrypter, ExecutorService executorService,
         PersistentStorageNameProvider storageNameProvider) {
      this.storageNameProvider = storageNameProvider;

      internalSnappyRepository = new PersistentInternalSnappyRepository(context, snappyCrypter, executorService);
   }

   @Override
   public void execute(SnappyAction action) {
      if (persistentStorageAvailable()) {
         internalSnappyRepository.execute(action);
      }
   }

   @Override
   public <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      if (persistentStorageAvailable()) {
         return internalSnappyRepository.executeWithResult(action);
      } else {
         return Optional.absent();
      }
   }

   private boolean persistentStorageAvailable() {
      updateStorageFolderName();
      return persistentStorageFolderName != null;
   }

   private void updateStorageFolderName() {
      persistentStorageFolderName = storageNameProvider.folderName();
   }

   /**
    * Provides SnappyDB instance located in internal storage. It must be always available for I/O.
    */
   private class PersistentInternalSnappyRepository extends WalletBaseSnappyRepository {

      PersistentInternalSnappyRepository(Context context, SnappyCrypter snappyCrypter, ExecutorService executorService) {
         super(context, snappyCrypter, executorService);
      }

      @Override
      protected DB openDbInstance(Context context) throws SnappydbException {
         return new SnappyDB.Builder(context)
               .directory(context.getFilesDir().getAbsolutePath() + "/persistent")
               .name(persistentStorageFolderName)
               .build();
      }
   }

}