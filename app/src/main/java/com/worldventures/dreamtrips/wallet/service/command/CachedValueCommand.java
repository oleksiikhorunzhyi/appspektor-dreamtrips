package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.wallet.util.NoActiveSmartCardException;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import rx.functions.Func0;
import rx.functions.Func1;

public abstract class CachedValueCommand<T> extends Command<T> implements CachedAction<T> {

   private final Func1<T, T> operationFunc;
   private final Func0<T> cacheFunc;
   private T result;

   public CachedValueCommand() {
      this.operationFunc = null;
      this.cacheFunc = null;
   }

   public CachedValueCommand(Func1<T, T> operationFunc) {
      this.operationFunc = operationFunc;
      this.cacheFunc = null;
   }

   public CachedValueCommand(Func0<T> cacheFunc) {
      this.cacheFunc = cacheFunc;
      this.operationFunc = null;
   }

   @Override
   protected final void run(CommandCallback<T> callback) throws Throwable {
      if (operationFunc != null) {
         if (result == null) throw new NoActiveSmartCardException("Active Smart Card does not exist.");
         result = operationFunc.call(result);
      } else if (cacheFunc != null) {
         result = cacheFunc.call();
      }
      callback.onSuccess(result);
   }

   @Override
   public final T getCacheData() {
      return getResult();
   }

   @Override
   public final void onRestore(ActionHolder holder, T cache) {
      this.result = cache;
   }

   @Override
   public final CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(hasOperationFunc())
            .build();
   }

   public boolean hasOperationFunc() {
      return operationFunc != null;
   }
}
