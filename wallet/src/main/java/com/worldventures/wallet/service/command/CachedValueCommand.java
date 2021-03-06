package com.worldventures.wallet.service.command;

import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;

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
   protected void run(CommandCallback<T> callback) throws Throwable {
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
      return new CacheOptions(true, hasOperationFunc(), true, null);
   }

   public boolean hasOperationFunc() {
      return operationFunc != null;
   }
}