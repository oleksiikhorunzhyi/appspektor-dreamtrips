package com.worldventures.core.janet.cache;

import com.worldventures.core.janet.cache.storage.MemoryStorage;
import com.worldventures.core.janet.cache.storage.Storage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionService;
import io.techery.janet.ActionServiceWrapper;
import io.techery.janet.JanetException;

public class CacheResultWrapper extends ActionServiceWrapper {

   private final Map<Class<? extends CachedAction>, Storage> storageMap = new HashMap<>();
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

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
            Object data;
            try {
               lock.readLock().lock();
               data = getStorage(actionClass).get(options.params());
               if (data != null) {
                  action.onRestore(holder, data);
                  return !options.sendAfterRestore();
               }
            } catch (Throwable throwable) {
               throw new JanetException("Action cannot be restored", throwable);
            } finally {
               lock.readLock().unlock();
            }
         }
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

   @SuppressWarnings("unchecked")
   @Override
   protected <A> void onInterceptSuccess(ActionHolder<A> holder) {
      if (holder.action() instanceof CachedAction) {
         CachedAction action = (CachedAction) holder.action();
         CacheOptions options = action.getCacheOptions();

         if (options.saveToCache()) {
            try {
               lock.writeLock().lock();
               getStorage(holder.action().getClass()).save(options.params(), action.getCacheData());
            } finally {
               lock.writeLock().unlock();
            }
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
