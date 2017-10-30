package com.worldventures.core.test.common;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

public final class AndroidRxJavaSchedulerInitializer extends RxAndroidSchedulersHook {

   static {
      RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
         @Override
         public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
         }
      });
   }

   private AndroidRxJavaSchedulerInitializer() {
   }

   public static void init() {
      //do nothing
   }
}
