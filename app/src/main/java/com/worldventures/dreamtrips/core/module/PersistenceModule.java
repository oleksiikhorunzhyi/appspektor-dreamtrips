package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.repository.SnappyRepositoryImpl;
import com.worldventures.dreamtrips.wallet.domain.storage.security.crypto.DreamTripsCrypter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class PersistenceModule {

   @Provides
   @Singleton
   public SnappyRepository provideDB(Context context, DreamTripsCrypter crypter) {
      return new SnappyRepositoryImpl(context, crypter);
   }
}
