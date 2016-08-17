package com.worldventures.dreamtrips.core.session;

import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;

public class CirclesInteractor {

   ActionPipe<CirclesCommand> pipe;

   public CirclesInteractor(Janet janet) {
      pipe = janet.createPipe(CirclesCommand.class);
   }

   public ActionPipe<CirclesCommand> pipe() {
      return pipe;
   }
}
