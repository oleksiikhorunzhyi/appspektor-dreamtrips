package com.worldventures.dreamtrips.core.janet.composer;

import io.techery.janet.ActionState;
import rx.Observable;

public class SuccessAndProgressComposer<T> implements Observable.Transformer<ActionState<T>, ActionState<T>> {

   @Override
   public Observable<ActionState<T>> call(Observable<ActionState<T>> actionStateObservable) {
      return actionStateObservable.flatMap(state -> {
         if (state.status == ActionState.Status.START) {
            return Observable.empty();
         }
         if (state.status == ActionState.Status.FAIL) {
            return Observable.error(state.exception);
         }
         return Observable.just(state);
      });
   }
}
