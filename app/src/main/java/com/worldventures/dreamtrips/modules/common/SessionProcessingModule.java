package com.worldventures.dreamtrips.modules.common;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SessionProcessingModule {

   @Singleton
   @Provides
   public AuthInteractor provideAuthInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AuthInteractor(sessionActionPipeCreator);
   }
}
