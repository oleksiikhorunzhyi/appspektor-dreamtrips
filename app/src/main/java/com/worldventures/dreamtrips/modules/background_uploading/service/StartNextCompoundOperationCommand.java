package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class StartNextCompoundOperationCommand extends Command<Void> implements InjectableAction, CachedAction<List<CompoundOperationModel>> {

   private List<CompoundOperationModel> existingUploads;

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      if (hasScheduledOperations() && !hasStartedOperations()) {
         backgroundUploadingInteractor.postProcessingPipe().send(new PostProcessingCommand(getNextOperation()));
      }
      callback.onSuccess(null);
   }

   private boolean hasScheduledOperations() {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.SCHEDULED);
   }

   private boolean hasStartedOperations() {
      return existingUploads != null && Queryable.from(existingUploads)
            .any(item -> item.state() == CompoundOperationState.STARTED);
   }

   private PostCompoundOperationModel getNextOperation() {
      return Queryable.from(existingUploads)
            .cast(PostCompoundOperationModel.class)
            .first(item -> item.state() == CompoundOperationState.SCHEDULED);
   }

   @Override
   public List<CompoundOperationModel> getCacheData() {
      return null;
   }

   @Override
   public void onRestore(ActionHolder holder, List<CompoundOperationModel> cache) {
      existingUploads = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(false)
            .build();
   }
}
