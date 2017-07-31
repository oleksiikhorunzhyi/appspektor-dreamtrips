package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.initializer.LostCardInitializer;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {LostCardInitializer.class},
      complete = false, library = true)
public class WalletInitializerModule {

   @Provides(type = Provides.Type.SET)
   AppInitializer provideLostCardInitializer(@ForApplication Context context) {
      return new LostCardInitializer(context);
   }
}
