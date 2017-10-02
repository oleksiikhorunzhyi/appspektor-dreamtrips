package com.worldventures.core.repository;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DefaultSnappyOpenHelper {

   private final ExecutorService executorService = Executors.newSingleThreadExecutor();

   public synchronized DB openDbInstance(Context context) throws SnappydbException {
      return DBFactory.open(context);
   }

   public ExecutorService provideExecutorService() {
      return executorService;
   }
}
