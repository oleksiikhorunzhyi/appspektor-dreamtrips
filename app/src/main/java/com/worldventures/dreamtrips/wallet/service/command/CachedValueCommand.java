package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.wallet.util.NoActiveSmartCardException;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import rx.functions.Func1;

public abstract class CachedValueCommand<T> extends Command<T> implements CachedAction<T> {

   private final Func1<T, T> operationFunc;
   private T result;

   public CachedValueCommand() {
      this.operationFunc = null;
   }

   public CachedValueCommand(Func1<T, T> operationFunc) {
      this.operationFunc = operationFunc;
   }

   @Override
   protected final void run(CommandCallback<T> callback) throws Throwable {
      if (result == null) {
         throw new NoActiveSmartCardException("Active Smart Card does not exist.");
      }
      if (operationFunc != null) {
         result = operationFunc.call(result);
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
