package com.worldventures.wallet.di;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.wallet.di.initializer.SnappyStorageManagerInitializer;
import com.worldventures.wallet.initializer.LostCardInitializer;
import com.worldventures.wallet.service.SmartCardInitializer;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            LostCardInitializer.class,
            SnappyStorageManagerInitializer.class,
            SmartCardInitializer.class,
      }, complete = false, library = true)
public class WalletInitializerModule {

   @Provides(type = Provides.Type.SET)
   AppInitializer provideLostCardInitializer() {
      return new LostCardInitializer();
   }

   @Provides(type = Provides.Type.SET)
   AppInitializer provideSmartCardInitializer() {
      return new SmartCardInitializer();
   }

   @Provides(type = Provides.Type.SET)
   AppInitializer provideSnappyStorageManagerInitializer() {
      return new SnappyStorageManagerInitializer();
   }

}
