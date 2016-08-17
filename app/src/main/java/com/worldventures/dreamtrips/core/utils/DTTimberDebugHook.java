package com.worldventures.dreamtrips.core.utils;

import com.worldventures.dreamtrips.core.rx.debug.DebugNotificationHelper;

import rx.plugins.DebugHook;
import rx.plugins.DebugNotification;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;
import timber.log.Timber;

public class DTTimberDebugHook extends DebugHook {
   /**
    * Creates a new instance of the DebugHook RxJava plug-in that can be passed into
    * {@link RxJavaPlugins} registerObservableExecutionHook(hook) method.
    */
   public DTTimberDebugHook() {
      super(new DebugNotificationListener<Void>() {
         @Override
         public <T> T onNext(DebugNotification<T> n) {
            Timber.v(n.toString());
            return super.onNext(n);
         }

         @Override
         public <T> Void start(DebugNotification<T> n) {
            if (n.getKind() == DebugNotification.Kind.OnError) {
               Timber.w(n.getThrowable(), "Observable problem: %s", DebugNotificationHelper.toString(n));
            }
            return super.start(n);
         }

         @Override
         public void error(Void context, Throwable e) {
            Timber.e(e, "Observable error, THREAD: %s", Thread.currentThread());
         }
      });
   }
}
