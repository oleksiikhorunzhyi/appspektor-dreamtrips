package com.messenger.delegate;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;

import javax.inject.Named;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class FlagsInteractor {

   private ActionPipe<GetFlagsCommand> flagsPipe;

   public FlagsInteractor(@Named(JanetModule.JANET_API_LIB) Janet janet) {
      flagsPipe = janet.createPipe(GetFlagsCommand.class, Schedulers.io());
   }

   public ActionPipe<GetFlagsCommand> getFlagsPipe() {
      return flagsPipe;
   }
}
