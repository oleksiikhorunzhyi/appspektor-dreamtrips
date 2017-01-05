package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.worldventures.dreamtrips.core.repository.SnappyCrypter;

import java.util.Collections;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class StorageModule {

   @Provides
   @Singleton
   public SnappyStorageManager SnappyRepositoryImpl(DiskStorage diskStorage, CardListStorage cardListStorage) {
      return new SnappyStorageManager(diskStorage, Collections.singletonList(cardListStorage));
   }

   @Provides
   @Singleton
   public CardListStorage cardListStorage(SnappyCrypter snappyCrypter) {
      return new CardListStorage(snappyCrypter);
   }

}
