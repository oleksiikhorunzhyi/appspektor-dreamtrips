package com.worldventures.core.modules.video.service.command;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.Status;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class ResetCachedModelsInProgressCommand extends Command implements InjectableAction {

   @Inject MediaModelStorage snappyDB;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      List<CachedModel> entities = snappyDB.getDownloadMediaModels();
      for (CachedModel entity : entities) {
         if (entity.getCacheStatus() == Status.IN_PROGRESS) {
            entity.setProgress(Status.IN_PROGRESS);
            entity.setProgress(0);
         }
         snappyDB.saveDownloadMediaModel(entity);
      }
      callback.onSuccess(null);
   }
}
