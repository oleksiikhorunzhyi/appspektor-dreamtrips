package com.worldventures.dreamtrips.core.rx.viewbinding;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;

import rx.Observable;

import static com.jakewharton.rxbinding.internal.Preconditions.checkNotNull;

public class DtRxBindings {

   /**
    * Create an observable of integers for {@link android.support.v4.view.ViewPager ViewPager} page position changes on {@code view}.
    * <p>
    * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
    * to free this reference.
    * <p>
    * <em>Note:</em> A value will <b>NOT</b> be emitted immediately on subscribe.
    */
   @CheckResult
   @NonNull
   public static Observable<Integer> observePageSelections(@NonNull ViewPager view) {
      checkNotNull(view, "view == null");
      return Observable.create(new ViewPagerPageSelectedOnSubscribe(view));
   }
}
