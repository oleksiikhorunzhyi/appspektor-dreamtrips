package com.worldventures.dreamtrips.modules.common.list_storage.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ListStorageCommand<T> extends Command<List<T>> implements CachedAction<List<T>> {

   private List<T> items = new ArrayList<>();
   private ListStorageOperation<T> operation;

   protected ListStorageCommand(ListStorageOperation<T> operation) {
      this.operation = operation;
   }

   @Override
   protected void run(CommandCallback<List<T>> callback) throws Throwable {
      callback.onSuccess(operation.perform(items));
   }

   @Override
   public List<T> getCacheData() {
      return new ArrayList<T>(items);
   }

   @Override
   public void onRestore(ActionHolder holder, List<T> cache) {
      if (cache != null) {
         items.clear();
         items.addAll(cache);
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions
            .builder()
            .params(new CacheBundleImpl())
            .build();
   }
}
