package com.worldventures.dreamtrips.wallet.util;

import java.util.concurrent.TimeUnit;

import io.techery.janet.ActionState;
import rx.Observable;

/**
 * Ensures minimum time {@link GuaranteedProgressVisibilityTransformer#progressGuaranteedVisibilityTime}
 * between {@link ActionState.Status#START} and {@link ActionState.Status#SUCCESS} or {@link ActionState.Status#FAIL}
 * so there will not be any flickering progress dialogs etc.
 */
public class GuaranteedProgressVisibilityTransformer<T extends ActionState> implements Observable.Transformer<T, T> {

   private static final long DEFAULT_GUARANTEED_PROGRESS_VISIBILITY_DURATION = 500;

   private long progressGuaranteedVisibilityTime = DEFAULT_GUARANTEED_PROGRESS_VISIBILITY_DURATION;
   private long startTime;

   public GuaranteedProgressVisibilityTransformer() {
   }

   public GuaranteedProgressVisibilityTransformer(long guaranteedVisibilityTime) {
      this.progressGuaranteedVisibilityTime = guaranteedVisibilityTime;
   }

   @Override
   public Observable<T> call(Observable<T> tObservable) {
      return tObservable.flatMap(actionState -> {
         if (actionState.status == ActionState.Status.START) {
            startTime = System.currentTimeMillis();
         }

         if (actionState.status == ActionState.Status.SUCCESS || actionState.status == ActionState.Status.FAIL) {
            long additionalDelay = Math.max(progressGuaranteedVisibilityTime - (System.currentTimeMillis() - startTime), 0);
            return Observable.timer(additionalDelay, TimeUnit.MILLISECONDS)
                  .flatMap(aLong -> Observable.just(actionState));
         } else {
            return Observable.just(actionState);
         }
      });
   }
}
