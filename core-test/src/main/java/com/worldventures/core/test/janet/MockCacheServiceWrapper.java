package com.worldventures.core.test.janet;

import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;

import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class MockCacheServiceWrapper extends ActionServiceWrapper {

   private final List<CacheContract> contracts;

   public MockCacheServiceWrapper(ActionService actionService, List<CacheContract> contracts) {
      super(actionService);
      this.contracts = contracts;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
      if (holder.action() instanceof CachedAction) {
         CachedAction action = (CachedAction) holder.action();
         CacheOptions options = action.getCacheOptions();

         if (options.getRestoreFromCache()) {
            Class<? extends CachedAction> actionClass = (Class<? extends CachedAction>) holder.action().getClass();
            CacheContract contract = getContract(actionClass);
            if (contract != null) {
               Object data = contract.cachedData;
               action.onRestore(holder, data);
               return !options.getSendAfterRestore();
            } else {
               return false;
            }

         }
      }
      return false;
   }

   private CacheContract getContract(Class<? extends CachedAction> actionClass) {
      for (CacheContract contract : contracts) {
         if (contract.cachedActionClass.equals(actionClass)) {
            return contract;
         }
      }
      return null;
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
      //do nothing
      return false;
   }

   public final static class CacheContract<T> {
      private final Class<? extends CachedAction<T>> cachedActionClass;
      private final T cachedData;

      public CacheContract(Class<? extends CachedAction<T>> cachedActionClass, T cachedData) {
         this.cachedActionClass = cachedActionClass;
         this.cachedData = cachedData;
      }
   }
}
