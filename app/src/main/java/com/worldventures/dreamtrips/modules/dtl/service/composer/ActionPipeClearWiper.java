package com.worldventures.dreamtrips.modules.dtl.service.composer;


import io.techery.janet.ActionState;
import io.techery.janet.ReadActionPipe;
import rx.Observable;

public final class ActionPipeClearWiper<A, T> implements Observable.Transformer<ActionState<A>, ActionState<A>> {

   private final ReadActionPipe<T> pipe;

   public ActionPipeClearWiper(ReadActionPipe<T> pipe) {
      this.pipe = pipe;
   }

   @Override
   public Observable<ActionState<A>> call(Observable<ActionState<A>> observable) {
      return observable.doOnNext(state -> {
         if (state.status == ActionState.Status.START) {
            pipe.clearReplays();
         }
      });
   }
}
