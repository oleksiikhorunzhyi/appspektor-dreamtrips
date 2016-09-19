package com.worldventures.dreamtrips.modules.flags;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;
import com.messenger.delegate.FlagsInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(
      injects = {
            GetFlagsCommand.class
      },
      library = true, complete = false)
public class FlagsModule {

   @Provides
   @Singleton
   FlagsInteractor provideFlagsProvider(Janet janet, SessionActionPipeCreator sessionActionPipeCreator) {
      return new FlagsInteractor(janet, sessionActionPipeCreator);
   }
}
