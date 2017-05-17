package com.worldventures.dreamtrips.common;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

public class AndroidRxJavaSchedulerInitializer extends RxAndroidSchedulersHook {

   static {
      RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
         @Override
         public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
         }
      });
   }

   public static void init() {

   }
}