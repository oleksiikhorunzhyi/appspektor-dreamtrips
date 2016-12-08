package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CompoundOperationsCommand extends Command<List<CompoundOperationModel>> implements CachedAction<List<CompoundOperationModel>> {

   private List<CompoundOperationModel> cachedModels = new ArrayList<>();

   private CompoundOperationModel updatedModel;

   private CompoundOperationsCommand(CompoundOperationModel updatedModel) {
      this.updatedModel = updatedModel;
   }

   @Override
   protected void run(CommandCallback<List<CompoundOperationModel>> callback) throws Throwable {
      processUpdatedModel();
      callback.onSuccess(cachedModels);
   }

   private void processUpdatedModel() {
      int index = cachedModels.size();
      for (int i = 0; i < cachedModels.size(); i++) {
         if (cachedModels.get(i).id() == updatedModel.id()) {
            cachedModels.remove(i);
            index = i;
            break;
         }
      }
      cachedModels.add(index, updatedModel);
   }

   @Override
   public List<CompoundOperationModel> getCacheData() {
      return cachedModels;
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
      return new CompoundOperationsCommand(updatedModel);
   }
}
