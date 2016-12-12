package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import timber.log.Timber;

@CommandAction
public class ScheduleCompoundOperationCommand extends Command<Void> implements InjectableAction, CachedAction<List<CompoundOperationModel>> {

   private PostCompoundOperationModel compoundOperationModel;

   private List<CompoundOperationModel> existingUploads = new ArrayList<>();

   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   public ScheduleCompoundOperationCommand(PostCompoundOperationModel compoundOperationModel) {
      this.compoundOperationModel = compoundOperationModel;
      existingUploads.add(compoundOperationModel);
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Timber.d("[New Post Creation] Scheduling new command, %s", existingUploads == null ? "No existing items" :
            existingUploads.toString());
      backgroundUploadingInteractor.compoundOperationsPipe()
            .send(CompoundOperationsCommand.compoundCommandChanged(compoundOperationModel));
      if (!hasStartedUploads()) {
         backgroundUploadingInteractor.startNextCompoundPipe().send(new StartNextCompoundOperationCommand());
      }
      callback.onSuccess(null);
   }

   private boolean hasStartedUploads() {
      return Queryable.from(existingUploads)
            .firstOrDefault(element -> element.state() == CompoundOperationState.STARTED) != null;
   }

   @Override
   public List<CompoundOperationModel> getCacheData() {
      return null;
   }

   @Override
   public void onRestore(ActionHolder holder, List<CompoundOperationModel> cache) {
      if (cache != null) {
         existingUploads.addAll(cache);
      }
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(false)
            .build();
   }
}
