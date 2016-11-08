package com.worldventures.dreamtrips.modules.flags.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.flags.command.FlagItemCommand;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FlagsInteractor {

   private ActionPipe<GetFlagsCommand> getFlagsPipe;
   private ActionPipe<FlagItemCommand> flagItemPipe;

   public FlagsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      getFlagsPipe = sessionActionPipeCreator.createPipe(GetFlagsCommand.class, Schedulers.io());
      flagItemPipe = sessionActionPipeCreator.createPipe(FlagItemCommand.class, Schedulers.io());
   }

   public ActionPipe<GetFlagsCommand> getFlagsPipe() {
      return getFlagsPipe;
   }

   public ActionPipe<FlagItemCommand> getFlagItemPipe() {
      return flagItemPipe;
   }
}
