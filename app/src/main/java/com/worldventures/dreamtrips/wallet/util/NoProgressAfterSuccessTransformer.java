package com.worldventures.dreamtrips.wallet.util;

import io.techery.janet.ActionState;
import rx.Observable;

/**
 * Ensures that after at least one {@link ActionState.Status#SUCCESS} all {@link ActionState.Status#START} events
 * will be skipped so there will not be a progress dialogs when data is already bind to UI.
 */
public class NoProgressAfterSuccessTransformer<T extends ActionState> implements Observable.Transformer<T, T> {

   private boolean hitSuccess = false;

   @Override
   public Observable<T> call(Observable<T> tObservable) {
      return tObservable.flatMap(actionState -> {
         if (actionState.status == ActionState.Status.SUCCESS) {
            hitSuccess = true;
         }

         if (hitSuccess && actionState.status == ActionState.Status.START) {
            return Observable.empty();
         }
         return Observable.just(actionState);
      });
   }
}
