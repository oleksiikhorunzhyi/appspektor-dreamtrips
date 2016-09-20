package com.worldventures.dreamtrips.core.janet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Scheduler;

public class SessionActionPipeCreator {

   private Set<ActionPipe> actionPipes = Collections.synchronizedSet(new HashSet<>());

   private Janet janet;

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
