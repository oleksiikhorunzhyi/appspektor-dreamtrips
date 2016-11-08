package com.worldventures.dreamtrips.core.janet.composer;

import io.techery.janet.ActionState;
import rx.Observable;

public final class TakeUntilStatusComposer<T> implements Observable.Transformer<ActionState<T>, ActionState<T>> {

   private final ActionState.Status[] statuses;

   public static <T> TakeUntilStatusComposer<T> create(ActionState.Status... statuses) {
      return new TakeUntilStatusComposer<>(statuses);
   }

   private TakeUntilStatusComposer(ActionState.Status... statuses) {this.statuses = statuses;}

   @Override
   public Observable<ActionState<T>> call(Observable<ActionState<T>> observable) {
      return observable.takeUntil(state -> {
         for (ActionState.Status status : statuses) {
            if (state.status == status)
               return true;
         }
         return false;
      });
   }
}
