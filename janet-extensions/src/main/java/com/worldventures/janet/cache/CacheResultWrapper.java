package com.worldventures.janet.cache;

import com.worldventures.janet.cache.storage.MemoryStorage;
import com.worldventures.janet.cache.storage.Storage;

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

         if (options.getRestoreFromCache()) {
            Class actionClass = holder.action().getClass();
            Object data;
            try {
               lock.readLock().lock();
               data = getStorage(actionClass).get(options.getParams());
               if (data != null) {
                  action.onRestore(holder, data);
                  return !options.getSendAfterRestore();
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

         if (options.getSaveToCache()) {
            try {
               lock.writeLock().lock();
               getStorage(holder.action().getClass()).save(options.getParams(), action.getCacheData());
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
