package com.worldventures.dreamtrips.modules.flags.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.ClearableStorage;
import com.worldventures.dreamtrips.core.janet.cache.storage.MemoryStorage;
import com.worldventures.dreamtrips.modules.flags.command.GetFlagsCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FlagsStorage implements ActionStorage<List<Flag>>, ClearableStorage {

   private final MemoryStorage<List<Flag>> memoryStorage;

   @Inject
   public FlagsStorage() {
      this.memoryStorage = new MemoryStorage<>();
   }

   @Override
   public Class<? extends CachedAction> getActionClass() {
      return GetFlagsCommand.class;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<Flag> data) {
      memoryStorage.save(params, data);
   }

   @Override
   public List<Flag> get(@Nullable CacheBundle action) {
      return memoryStorage.get(action);
   }

   @Override
   public void clearMemory() {
      memoryStorage.clearMemory();
   }
}
