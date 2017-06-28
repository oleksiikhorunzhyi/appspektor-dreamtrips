package com.worldventures.dreamtrips.core.repository;

import android.content.Context;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.SnappyStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class SnappyModule {

   public static final String SNAPPY_STORAGE_EXECUTOR_SERVICE = "SnappyStorageExecutorService";

   @Provides
   @Singleton
   SnappyRepositoryImpl snappyRepositoryImpl(@ForApplication Context appContext, SnappyCrypter snappyCrypter,
         @Named(SNAPPY_STORAGE_EXECUTOR_SERVICE) ExecutorService executorService) {
      return new SnappyRepositoryImpl(appContext, snappyCrypter, executorService);
   }

   @Provides
   @Singleton
   public SnappyRepository snappyRepository(SnappyRepositoryImpl snappyRepository) {
      return snappyRepository;
   }

   @Provides
   @Singleton
   public SnappyStorage diskStorage(SnappyRepositoryImpl snappyRepository) {
      return snappyRepository;
   }

   @Provides
   @Singleton
   public SnappyCrypter snappyCrypter(Kryo kryo, HybridAndroidCrypter crypter) {
      return new SnappyCrypter(kryo, crypter);
   }

   @Provides
   @Singleton
   public Kryo kryo() {
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
   @Named(SNAPPY_STORAGE_EXECUTOR_SERVICE)
   public ExecutorService provideSnappyRepositoryExecutorService() {
      return Executors.newSingleThreadExecutor();
   }

}