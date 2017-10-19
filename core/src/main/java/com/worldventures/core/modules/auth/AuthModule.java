package com.worldventures.core.modules.auth;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LoginCommand;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            LoginCommand.class,
            LogoutCommand.class,

      },
      complete = false,
      library = true)
public class AuthModule {

   @Singleton
   @Provides
   public AuthInteractor provideAuthInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new AuthInteractor(sessionActionPipeCreator);
   }
}
