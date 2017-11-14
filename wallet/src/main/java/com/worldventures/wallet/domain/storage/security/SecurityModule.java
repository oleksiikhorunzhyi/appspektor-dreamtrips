package com.worldventures.wallet.domain.storage.security;


import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.wallet.domain.storage.security.crypto.Crypter;
import com.worldventures.wallet.domain.storage.security.crypto.HybridAndroidCrypter;
import com.worldventures.wallet.domain.storage.security.crypto.HybridAndroidCrypter.AsymmetricKeyParams;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.wallet.domain.storage.security.crypto.HybridAndroidCrypter.SymmetricKeyParams;

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
