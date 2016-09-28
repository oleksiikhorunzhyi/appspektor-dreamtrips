package com.worldventures.dreamtrips.modules.flags.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FlagsInteractor {

   private ActionPipe<GetFlagsCommand> flagsPipe;

   public FlagsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      flagsPipe = sessionActionPipeCreator.createPipe( GetFlagsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetFlagsCommand> getFlagsPipe() {
      return flagsPipe;
   }
}
