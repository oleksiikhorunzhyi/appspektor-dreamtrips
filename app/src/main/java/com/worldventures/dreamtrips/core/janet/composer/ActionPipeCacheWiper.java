package com.worldventures.dreamtrips.core.janet.composer;

import io.techery.janet.ActionState;
import io.techery.janet.ReadActionPipe;
import rx.Observable;

public class ActionPipeCacheWiper<A> implements Observable.Transformer<ActionState<A>, ActionState<A>> {

   private final ReadActionPipe<A> pipe;

   public ActionPipeCacheWiper(ReadActionPipe<A> pipe) {
      this.pipe = pipe;
   }

   @Override
   public Observable<ActionState<A>> call(Observable<ActionState<A>> observable) {
      return observable.doOnNext(state -> {
         if (state.status != ActionState.Status.START
               && state.status != ActionState.Status.PROGRESS) {
            pipe.clearReplays();
         }
      });
   }
}
