package com.worldventures.dreamtrips.wallet.domain.storage.disk;

import com.worldventures.dreamtrips.core.repository.SnappyCrypter;
import com.worldventures.dreamtrips.wallet.domain.storage.persistent.PersistentSnappyModule;

import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.dreamtrips.wallet.domain.storage.persistent.PersistentSnappyModule.PERSISTENT_SNAPPY_STORAGE;

@Module(includes = {PersistentSnappyModule.class}, complete = false, library = true)
public class StorageModule {

   @Provides
   @Singleton
   public SnappyStorageManager SnappyRepositoryImpl(CardListStorage cardListStorage) {
      return new SnappyStorageManager(Collections.singletonList(cardListStorage));
   }

   @Provides
   @Singleton
   public CardListStorage cardListStorage(@Named(PERSISTENT_SNAPPY_STORAGE) SnappyStorage snappyStorage, SnappyCrypter snappyCrypter) {
      return new CardListStorage(snappyStorage, snappyCrypter);
   }

}