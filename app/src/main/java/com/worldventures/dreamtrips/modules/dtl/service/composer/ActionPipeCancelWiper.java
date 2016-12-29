package com.worldventures.dreamtrips.modules.dtl.service.composer;


import io.techery.janet.ActionPipe;
import io.techery.janet.ActionState;
import rx.Observable;

public final class ActionPipeCancelWiper<A, T> implements Observable.Transformer<ActionState<A>, ActionState<A>> {

   private final ActionPipe<T> pipe;

   public ActionPipeCancelWiper(ActionPipe<T> pipe) {
      this.pipe = pipe;
   }

   @Override
   public Observable<ActionState<A>> call(Observable<ActionState<A>> observable) {
      return observable.doOnNext(state -> {
         if (state.status == ActionState.Status.START) {
            pipe.cancelLatest();
         }
      });
   }
}
