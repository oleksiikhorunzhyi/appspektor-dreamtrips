package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;

public abstract class CompoundOperationsCommand extends Command<List<CompoundOperationModel>>
      implements CachedAction<List<CompoundOperationModel>> {

   protected List<CompoundOperationModel> cachedModels = new ArrayList<>();

   @Override
   public List<CompoundOperationModel> getCacheData() {
      return new ArrayList<>(cachedModels);
   }

   @Override
   public void onRestore(ActionHolder holder, List<CompoundOperationModel> cache) {
      if (cache != null) {
         cachedModels.addAll(cache);
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .build();
   }

   public static CompoundOperationsCommand compoundCommandChanged(CompoundOperationModel updatedModel) {
      return new UpdateCompoundOperationsCommand(updatedModel);
   }

   public static CompoundOperationsCommand compoundCommandRemoved(CompoundOperationModel updatedModel) {
      return new DeleteCompoundOperationsCommand(updatedModel);
   }
}
