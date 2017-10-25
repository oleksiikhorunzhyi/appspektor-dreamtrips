package com.worldventures.core.repository;

import android.content.Context;
import android.support.annotation.Nullable;

import com.snappydb.DB;
import com.snappydb.SnappydbException;
import com.worldventures.core.storage.complex_objects.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import timber.log.Timber;

public abstract class BaseSnappyRepository {

   protected final Context context;
   private final ExecutorService executorService;

   protected BaseSnappyRepository(Context context, ExecutorService executorService) {
      this.context = context;
      this.executorService = executorService;
   }

   protected <T> void putList(String key, Collection<T> list) {
      act(db -> db.put(key, list.toArray()));
   }

   protected <T> List<T> readList(String key, Class<T> clazz) {
      return actWithResult(db -> new ArrayList<>(Arrays.asList(db.getObjectArray(key, clazz))))
            .or(new ArrayList<>());
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

   private boolean isNotFound(SnappydbException e) {
      return e.getMessage().contains("NotFound");
   }

}