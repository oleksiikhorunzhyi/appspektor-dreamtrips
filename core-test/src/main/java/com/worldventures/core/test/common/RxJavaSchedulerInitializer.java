package com.worldventures.core.test.common;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public final class RxJavaSchedulerInitializer {
   static {
      RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
   }

   private RxJavaSchedulerInitializer() {
   }

   public static void init() {
      //do nothing
   }
}
