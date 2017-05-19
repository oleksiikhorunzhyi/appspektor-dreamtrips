package com.worldventures.dreamtrips.wallet.domain.storage.persistent;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappyDB;
import com.snappydb.SnappydbException;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.repository.BaseSnappyRepository;
import com.worldventures.dreamtrips.core.repository.SnappyAction;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.core.repository.SnappyResult;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorage;

import java.util.concurrent.ExecutorService;

/**
 * Repository is not cleared on logout.
 */
public class PersistentSnappyRepositoryImpl implements SnappyStorage {

   private final SnappyStorage internalSnappyRepository;

   private final SessionHolder<UserSession> sessionHolder;

   @Nullable
   private String persistentStorageFolderName;

   public PersistentSnappyRepositoryImpl(Context context, SnappyCrypter snappyCrypter, ExecutorService executorService,
         SessionHolder<UserSession> sessionHolder) {
      this.sessionHolder = sessionHolder;

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
      String userId = getUserId();

      persistentStorageFolderName = (userId != null) ? String.valueOf((userId).hashCode()) : null;
   }

   @Nullable
   private String getUserId() {
      User user = sessionHolder.get().isPresent() ? sessionHolder.get().get().getUser() : null;
      return (user != null) ? String.valueOf(user.getId()) : null;
   }

   /**
    * Provides SnappyDB instance located in internal storage. It must be always available for I/O.
    */
   private class PersistentInternalSnappyRepository extends BaseSnappyRepository {

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