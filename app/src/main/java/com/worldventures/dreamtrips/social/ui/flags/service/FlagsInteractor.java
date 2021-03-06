package com.worldventures.dreamtrips.social.ui.flags.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.flags.command.FlagItemCommand;
import com.worldventures.dreamtrips.social.ui.flags.command.GetFlagsCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FlagsInteractor {

   private final ActionPipe<GetFlagsCommand> getFlagsPipe;
   private final ActionPipe<FlagItemCommand> flagItemPipe;

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
