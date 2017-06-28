package com.worldventures.dreamtrips.janet;

import com.worldventures.dreamtrips.common.Injector;
import com.worldventures.dreamtrips.common.ObjectProvider;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.lang.reflect.Field;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class MockDaggerActionService extends ActionServiceWrapper {

   private final Injector injector = new Injector();

   public MockDaggerActionService(ActionService actionService) {
      super(actionService);
   }

   public <T> MockDaggerActionService registerProvider(Class<T> objClass, ObjectProvider<T> provider) {
      injector.registerProvider(objClass, provider);
      return this;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
      A action = holder.action();
      if (!(action instanceof InjectableAction)) return false;
      try {
         InjectableAction injectableAction = (InjectableAction) action;
         injector.inject(injectableAction);
      } catch (Throwable throwable) {
         Timber.e(throwable, "Can't inject action %s", action);
      }
      return false;
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {
   }

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {
   }

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      return false;
   }
}
