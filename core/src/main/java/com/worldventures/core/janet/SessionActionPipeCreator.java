package com.worldventures.core.janet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Scheduler;

public class SessionActionPipeCreator {

   private final Set<ActionPipe> actionPipes = Collections.synchronizedSet(new HashSet<ActionPipe>());
   private final Janet janet;

   public SessionActionPipeCreator(Janet janet) {
      this.janet = janet;
   }

   public <A> ActionPipe<A> createPipe(Class<A> actionClass) {
      return createPipe(actionClass, null);
   }

   public <A> ActionPipe<A> createPipe(Class<A> actionClass, Scheduler defaultSubscribeOn) {
      ActionPipe<A> actionPipe = janet.createPipe(actionClass, defaultSubscribeOn);
      actionPipes.add(actionPipe);
      return actionPipe;
   }

   public void clearReplays() {
      for (ActionPipe actionPipe : actionPipes) {
         actionPipe.clearReplays();
      }
   }
}
