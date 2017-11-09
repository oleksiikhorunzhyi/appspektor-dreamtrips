package com.worldventures.wallet.domain.storage;

import android.content.Context;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.worldventures.core.repository.DefaultSnappyOpenHelper;
import com.worldventures.wallet.domain.storage.disk.FirmwareDataStorage;
import com.worldventures.wallet.domain.storage.disk.PersistentFirmwareDataStorage;
import com.worldventures.wallet.domain.storage.disk.PersistentRecordsStorage;
import com.worldventures.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.wallet.domain.storage.disk.SnappyStorage;
import com.worldventures.wallet.domain.storage.disk.SnappyStorageManager;
import com.worldventures.wallet.domain.storage.persistent.PersistentSnappyModule;
import com.worldventures.wallet.domain.storage.security.SecurityModule;
import com.worldventures.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.Collections;
import java.util.Date;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.wallet.domain.storage.persistent.PersistentSnappyModule.PERSISTENT_SNAPPY_STORAGE;

@Module(includes = {
      PersistentSnappyModule.class,
      SecurityModule.class
}, complete = false, library = true)
public class WalletStorageModule {

   @Provides
   @Singleton
   WalletStorageImpl walletStorageImpl(Context context, SnappyCrypter snappyCrypter, DefaultSnappyOpenHelper defaultSnappyOpenHelper) {
      return new WalletStorageImpl(context, snappyCrypter, defaultSnappyOpenHelper);
   }

   @Provides
   WalletStorage walletStorage(WalletStorageImpl walletStorage) {
      return walletStorage;
   }

   @Provides
   SnappyStorage diskStorage(WalletStorageImpl snappyStorag) {
      return snappyStorag;
   }

   @Provides
   SnappyCrypter snappyCrypter(HybridAndroidCrypter crypter) {
      return new SnappyCrypter(provideKryo(), crypter);
   }

   private Kryo provideKryo() {
      final Kryo.DefaultInstantiatorStrategy strategy = new Kryo.DefaultInstantiatorStrategy();
      strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

      final Kryo kryo = new Kryo();
      kryo.register(Date.class, new DefaultSerializers.DateSerializer());
      kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
      kryo.setInstantiatorStrategy(strategy);
      return kryo;
   }

   @Provides
   @Singleton
   SnappyStorageManager SnappyRepositoryImpl(RecordsStorage recordsStorage) {
      return new SnappyStorageManager(Collections.singletonList(recordsStorage));
   }

   @Provides
   @Singleton
   RecordsStorage persistentCardListStorage(@Named(PERSISTENT_SNAPPY_STORAGE) SnappyStorage snappyStorage, SnappyCrypter snappyCrypter) {
      return new PersistentRecordsStorage(snappyStorage, snappyCrypter);
   }

   @Provides
   @Singleton
   FirmwareDataStorage provideWalletPersistentStorage(@Named(PERSISTENT_SNAPPY_STORAGE) SnappyStorage snappyStorage, SnappyCrypter snappyCrypter) {
      return new PersistentFirmwareDataStorage(snappyStorage, snappyCrypter);
   }

}
