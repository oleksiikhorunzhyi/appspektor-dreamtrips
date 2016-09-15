package com.worldventures.dreamtrips.common;

import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public final class RxJavaSchedulerInitializer {
   static {
      RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
         @Override
         public Scheduler getIOScheduler() {
            return Schedulers.immediate();
         }
      });
   }

   public static void init() {}
}