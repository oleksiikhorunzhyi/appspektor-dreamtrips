package com.worldventures.dreamtrips.modules.flags;

import com.worldventures.dreamtrips.modules.flags.service.FlagsInteractor;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {
            GetFlagsCommand.class
      },
      library = true, complete = false)
public class FlagsModule {

   @Provides
   @Singleton
   FlagsInteractor provideFlagsProvider(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FlagsInteractor(sessionActionPipeCreator);
   }
}
