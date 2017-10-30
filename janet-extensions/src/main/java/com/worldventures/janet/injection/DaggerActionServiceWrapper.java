package com.worldventures.janet.injection;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class DaggerActionServiceWrapper extends ActionServiceWrapper {

   private final CommandInjector injector;
   private final ActionServiceLogger serviceLogger;

   public DaggerActionServiceWrapper(ActionService service, CommandInjector commandInjector, ActionServiceLogger serviceLogger) {
      super(service);
      this.injector = commandInjector;
      this.serviceLogger = serviceLogger;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      A action = holder.action();
      if (!(action instanceof InjectableAction)) {
         return false;
      }
      try {
         injector.inject(action);
      } catch (Throwable throwable) {
         serviceLogger.error(throwable, "Can't inject action %s", action);
      }
      return false;
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
      //do nothing
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
      //do nothing
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      return false;
   }
}
