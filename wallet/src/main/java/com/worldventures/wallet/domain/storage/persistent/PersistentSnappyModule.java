package com.worldventures.wallet.domain.storage.persistent;

import android.content.Context;

import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.wallet.domain.storage.SnappyCrypter;
import com.worldventures.wallet.domain.storage.disk.SnappyStorage;
import com.worldventures.wallet.service.WalletSocialInfoProvider;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class PersistentSnappyModule {

   public static final String PERSISTENT_SNAPPY_STORAGE = "persistentSnappyStorage";

   @Provides
   @Singleton
   PersistentSnappyRepositoryImpl persistentSnappyRepositoryImpl(Context appContext, SnappyCrypter snappyCrypter,
         DefaultSnappyOpenHelper defaultSnappyOpenHelper, WalletSocialInfoProvider socialInfoProvider) {
      return new PersistentSnappyRepositoryImpl(appContext, snappyCrypter, defaultSnappyOpenHelper.provideExecutorService(),
            () -> socialInfoProvider.hasUser() ? String.valueOf(Integer.valueOf(socialInfoProvider.userId()).hashCode()) : null);
   }

   @Provides
   @Singleton
   @Named(PERSISTENT_SNAPPY_STORAGE)
   SnappyStorage persistentDiskStorage(PersistentSnappyRepositoryImpl walletStorage) {
      return walletStorage;
   }

}