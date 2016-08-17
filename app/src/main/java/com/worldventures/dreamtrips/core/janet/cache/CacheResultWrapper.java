package com.worldventures.dreamtrips.core.janet.cache;

import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.Storage;

import java.util.HashMap;
import java.util.Map;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class CacheResultWrapper extends ActionServiceWrapper {

   private Map<Class<? extends CachedAction>, Storage> storageMap = new HashMap<>();

   public CacheResultWrapper(ActionService actionService) {
      super(actionService);
   }

   public CacheResultWrapper bindStorage(Class<? extends CachedAction> actionClass, Storage storage) {
      storageMap.put(actionClass, storage);
      return this;
   }

   @Override
   protected <A> boolean onInterceptSend(ActionHolder<A> holder) throws JanetException {
      if (holder.action() instanceof CachedAction) {
         CachedAction action = (CachedAction) holder.action();
         CacheOptions options = action.getCacheOptions();

         if (options.restoreFromCache()) {
            Class actionClass = holder.action().getClass();
            Object data = getStorage(actionClass).get(options.params());
            if (data != null) {
               action.onRestore(holder, data);
               return !options.sendAfterRestore();
            }
         }
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

   @SuppressWarnings("unchecked")
   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
      if (holder.action() instanceof CachedAction) {
         CachedAction action = (CachedAction) holder.action();
         CacheOptions options = action.getCacheOptions();

         if (options.saveToCache()) {
            getStorage(holder.action().getClass()).save(options.params(), action.getCacheData());
         }
      }
   }

   @Override
   protected <A> boolean onInterceptFail(ActionHolder<A> holder, JanetException e) {
      return false;
   }

   private Storage getStorage(Class actionClass) {
      Storage storage = storageMap.get(actionClass);
      if (storage == null) {
         storage = new MemoryStorage<>();
         storageMap.put(actionClass, storage);
      }
      return storage;
   }
}
