package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.janet.cache.CacheOptions;
import com.worldventures.janet.cache.CachedAction;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;

public abstract class CompoundOperationsCommand extends Command<List<PostCompoundOperationModel>>
      implements CachedAction<List<PostCompoundOperationModel>> {

   protected List<PostCompoundOperationModel> cachedModels = new ArrayList<>();

   @Override
   public List<PostCompoundOperationModel> getCacheData() {
      if (getResult() != null) {
         return new ArrayList<>(getResult());
      }
      return new ArrayList<>(cachedModels);
   }

   @Override
   public void onRestore(ActionHolder holder, List<PostCompoundOperationModel> cache) {
      if (cache != null) {
         cachedModels.addAll(cache);
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return new CacheOptions();
   }

   public static CompoundOperationsCommand compoundOperationsChanged(List<PostCompoundOperationModel> updatedModels) {
      return new UpdateCompoundOperationsCommand(updatedModels);
   }

   public static CompoundOperationsCommand compoundCommandChanged(PostCompoundOperationModel updatedModel) {
      return new UpdateCompoundOperationCommand(updatedModel);
   }

   public static CompoundOperationsCommand compoundCommandRemoved(PostCompoundOperationModel updatedModel) {
      return new DeleteCompoundOperationsCommand(updatedModel);
   }
}
