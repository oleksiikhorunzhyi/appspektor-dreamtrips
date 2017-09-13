package com.worldventures.dreamtrips.common;

import rx.plugins.RxJavaHooks;
import rx.schedulers.Schedulers;

public final class RxJavaSchedulerInitializer {
   static {
      RxJavaHooks.setOnIOScheduler(scheduler -> Schedulers.immediate());
   }

   public static void init() {}
}