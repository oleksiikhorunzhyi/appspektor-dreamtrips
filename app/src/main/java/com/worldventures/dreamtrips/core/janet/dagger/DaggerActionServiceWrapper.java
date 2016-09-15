package com.worldventures.dreamtrips.core.janet.dagger;

import android.content.Context;

import com.techery.spares.module.Injector;

import dagger.ObjectGraph;
import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;
import timber.log.Timber;

public class DaggerActionServiceWrapper extends ActionServiceWrapper {

   private final CommandInjector injector;

   public DaggerActionServiceWrapper(ActionService service, Context appContext) {
      super(service);
      this.injector = new CommandInjector(((Injector) appContext).getObjectGraph());
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) {
      A action = holder.action();
      if (!(action instanceof InjectableAction)) return false;
      try {
         injector.inject(action);
      } catch (Throwable throwable) {
         Timber.e(throwable, "Can't inject action %s", action);
      }
      return false;
   }

   @Override
   protected <A> void onInterceptCancel(ActionHolder<A> holder) {}

   @Override
   protected <A> void onInterceptStart(ActionHolder<A> holder) {}

   @Override
   protected <A> void onInterceptProgress(ActionHolder<A> holder, int progress) {}

   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {}

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      return false;
   }

   private static class CommandInjector {

      private ObjectGraph objectGraph;

      private CommandInjector(ObjectGraph objectGraph) {
         this.objectGraph = objectGraph;
      }

      public void inject(Object action) {
         try {
            objectGraph.inject(action);
         } catch (Throwable e) {
            String detailMessage = "No graph method found to inject " + action.getClass()
                  .getSimpleName() + ". Check your component";
            Timber.e(e, detailMessage);
            throw e;
         }
      }
   }
}
