package com.worldventures.dreamtrips.wallet.domain.storage.persistent;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorage;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.dreamtrips.core.repository.SnappyModule.SNAPPY_STORAGE_EXECUTOR_SERVICE;

@Module(complete = false, library = true)
public class PersistentSnappyModule {

   public static final String PERSISTENT_SNAPPY_STORAGE = "persistentSnappyStorage";

   @Provides
   @Singleton
   PersistentSnappyRepositoryImpl persistentSnappyRepositoryImpl(@ForApplication Context appContext, SnappyCrypter snappyCrypter,
         @Named(SNAPPY_STORAGE_EXECUTOR_SERVICE) ExecutorService executorService,
         SessionHolder<UserSession> sessionHolder) {
      return new PersistentSnappyRepositoryImpl(appContext, snappyCrypter, executorService, sessionHolder);
   }

   @Provides
   @Singleton
   @Named(PERSISTENT_SNAPPY_STORAGE)
   public SnappyStorage persistentDiskStorage(PersistentSnappyRepositoryImpl snappyRepository) {
      return snappyRepository;
   }

}