package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.QueryTripsFilterDataInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(library = true, complete = false)
public class SessionProcessingModule {

   @Provides
   @Singleton
   public QueryTripsFilterDataInteractor provideQueryTripsFilterDataInteractor(Janet janet) {
      return new QueryTripsFilterDataInteractor(janet);
   }

   @Singleton
   @Provides
   public AuthInteractor provideAuthInteractor(Janet janet) {
      return new AuthInteractor(janet);
   }
}
