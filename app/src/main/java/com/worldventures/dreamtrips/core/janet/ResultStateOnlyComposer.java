package com.worldventures.dreamtrips.core.janet;

import io.techery.janet.ActionState;
import rx.Observable;

public class ResultStateOnlyComposer<T> implements Observable.Transformer<ActionState<T>, ActionState<T>> {

   private static final ResultStateOnlyComposer INSTANCE = new ResultStateOnlyComposer();

   public static <T> ResultStateOnlyComposer<T> instance() {
      return (ResultStateOnlyComposer<T>) INSTANCE;
   }

   @Override
   public Observable<ActionState<T>> call(Observable<ActionState<T>> observable) {
      return observable.filter(actionState -> actionState.status == ActionState.Status.SUCCESS || actionState.status == ActionState.Status.FAIL);
   }
}
