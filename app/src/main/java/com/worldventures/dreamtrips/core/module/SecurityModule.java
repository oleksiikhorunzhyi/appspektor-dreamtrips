package com.worldventures.dreamtrips.core.module;


import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.Crypter;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter.AsymmetricKeyParams;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.HybridAndroidCrypter.*;

@Module(library = true, complete = false)
public class SecurityModule {

   @Provides
   @Singleton
   HybridAndroidCrypter provideCrypter(@ForApplication Context context) {
      return new HybridAndroidCrypter(context, "DreamTrips",
            new AsymmetricKeyParams("RSA/ECB/PKCS1Padding", "RSA", "CN=DreamTrips, O=WorldVentures", 1024),
            new SymmetricKeyParams("AES", 256)
      );
   }

   @Provides
   Crypter provideCrypter(HybridAndroidCrypter crypter) {
      return crypter;
   }
}
