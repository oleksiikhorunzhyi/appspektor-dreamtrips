package com.worldventures.dreamtrips.core.repository;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import timber.log.Timber;

public abstract class BaseSnappyRepository implements SnappyStorage {

   protected final Context context;

   private final SnappyCrypter snappyCrypter;
   private final ExecutorService executorService;

   protected BaseSnappyRepository(Context context, SnappyCrypter snappyCrypter, ExecutorService executorService) {
      this.context = context;
      this.snappyCrypter = snappyCrypter;
      this.executorService = executorService;
   }

   @Override
   public void execute(SnappyAction action) {
      act(action);
   }

   @Override
   public <T> Optional<T> executeWithResult(SnappyResult<T> action) {
      return actWithResult(action);
   }

   protected void act(SnappyAction action) {
      executorService.execute(() -> {
         DB snappyDb = null;
         try {
            snappyDb = openDbInstance(context);
            action.call(snappyDb);
         } catch (SnappydbException e) {
            if (isNotFound(e)) Timber.v("Nothing found");
            else Timber.w(e, "DB fails to act");
         } catch (NullPointerException e) {
            Timber.v(e, "Snappy instance is null");
         } finally {
            try {
               if (snappyDb != null && snappyDb.isOpen()) snappyDb.close();
            } catch (SnappydbException e) {
               Timber.w(e, "DB fails to close");
            }
         }
      });
   }

   protected <T> Optional<T> actWithResult(SnappyResult<T> action) {
      Future<T> future = executorService.submit(() -> {
         DB snappyDb = null;
         try {
            snappyDb = openDbInstance(context);
            T result = action.call(snappyDb);
            Timber.v("DB action result: %s", result);
            return result;
         } catch (SnappydbException e) {
            if (isNotFound(e)) Timber.v("Nothing found");
            else Timber.w(e, "DB fails to act with result");
            return null;
         } catch (NullPointerException e) {
            Timber.v(e, "Snappy instance is null");
            return null;
         } finally {
            try {
               if (snappyDb != null && snappyDb.isOpen()) snappyDb.close();
            } catch (SnappydbException e) {
               Timber.w(e, "DB fails to close");
            }
         }
      });
      try {
         return Optional.fromNullable(future.get());
      } catch (InterruptedException | ExecutionException e) {
         Timber.w(e, "DB fails to act with result");
         return Optional.absent();
      }
   }

   @Nullable
   protected abstract DB openDbInstance(Context context) throws SnappydbException;

   protected void putEncrypted(String key, Object obj) {
      act(db -> snappyCrypter.putEncrypted(db, key, obj));
   }

   protected <T> T getEncrypted(String key, Class<T> clazz) {
      return actWithResult(db -> snappyCrypter.getEncrypted(db, key, clazz)).orNull();
   }

   protected boolean isNotFound(SnappydbException e) {
      return e.getMessage().contains("NotFound");
   }

}