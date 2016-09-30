package com.worldventures.dreamtrips.core.module;


import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.Crypter;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.DreamTripsCrypter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SecurityModule {

   @Provides
   @Singleton
   DreamTripsCrypter provideCrypter(@ForApplication Context context) {
      return new DreamTripsCrypter(context, "DreamTrips");
   }

   @Provides
   Crypter provideCrypter(DreamTripsCrypter crypter) {
      return crypter;
   }
}
