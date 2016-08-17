package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter.rx;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

import static rx.android.MainThreadSubscription.verifyMainThread;

public class DrawerToggleObservable implements Observable.OnSubscribe<Integer> {

   private final DrawerLayout drawerLayout;

   public static Observable<Integer> create(DrawerLayout drawer) {
      return Observable.create(new DrawerToggleObservable(drawer));
   }

   public DrawerToggleObservable(DrawerLayout drawerLayout) {
      this.drawerLayout = drawerLayout;
   }

   @Override
   public void call(final Subscriber<? super Integer> subscriber) {
      verifyMainThread();

      DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
         @Override
         public void onDrawerSlide(View drawerView, float slideOffset) {

         }

         @Override
         public void onDrawerOpened(View drawerView) {
            if (!subscriber.isUnsubscribed()) {
               subscriber.onNext(drawerLayout.isDrawerOpen(Gravity.LEFT) ? Gravity.LEFT : Gravity.RIGHT);
            }
         }

         @Override
         public void onDrawerClosed(View drawerView) {
            if (!subscriber.isUnsubscribed()) {
               subscriber.onNext(Gravity.NO_GRAVITY);
            }
         }

         @Override
         public void onDrawerStateChanged(int newState) {

         }
      };

      drawerLayout.addDrawerListener(listener);


      subscriber.add(new MainThreadSubscription() {
         @Override
         protected void onUnsubscribe() {
            drawerLayout.removeDrawerListener(listener);
         }
      });
   }
}
