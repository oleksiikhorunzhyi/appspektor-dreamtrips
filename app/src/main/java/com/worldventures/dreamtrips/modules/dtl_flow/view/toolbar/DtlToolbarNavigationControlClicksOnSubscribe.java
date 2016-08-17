package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class DtlToolbarNavigationControlClicksOnSubscribe implements Observable.OnSubscribe<Void> {

   private final ExpandableDtlToolbar dtlToolbar;

   public DtlToolbarNavigationControlClicksOnSubscribe(ExpandableDtlToolbar dtlToolbar) {
      this.dtlToolbar = dtlToolbar;
   }

   @Override
   public void call(Subscriber<? super Void> subscriber) {
      ExpandableDtlToolbar.NavigationControlListener navigationControlListener = new ExpandableDtlToolbar.NavigationControlListener() {
         @Override
         public void onNavigationControlClicked() {
            if (!subscriber.isUnsubscribed()) {
               subscriber.onNext(null);
            }
         }
      };

      dtlToolbar.addNavigationControlClickListener(navigationControlListener);

      subscriber.add(new MainThreadSubscription() {
         @Override
         protected void onUnsubscribe() {
            dtlToolbar.removeNavigationControlClickListener(navigationControlListener);
         }
      });
   }
}
