package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ResetCachedModelsInProgressCommand extends Command implements InjectableAction {

   @Inject SnappyRepository snappyDB;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      List<CachedModel> entities = snappyDB.getDownloadMediaModels();
      for (CachedModel entity : entities) {
         if (entity.inProgress()) {
            entity.setIsFailed(true);
            entity.setProgress(0);
         }
         snappyDB.saveDownloadMediaModel(entity);
      }
      callback.onSuccess(null);
   }
}
