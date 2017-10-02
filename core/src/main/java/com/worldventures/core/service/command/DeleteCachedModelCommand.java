package com.worldventures.core.service.command;

import com.worldventures.core.R;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.model.Status;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCachedModelCommand extends CachedEntityCommand implements InjectableAction {

   private File file;

   @Inject MediaModelStorage db;

   public DeleteCachedModelCommand(CachedModel cachedModel, File file) {
      super(cachedModel);
      this.file = file;
   }

   @Override
   protected void run(Command.CommandCallback<CachedModel> callback) throws Throwable {
      file.delete();
      cachedModel.setProgress(0);
      cachedModel.setCacheStatus(Status.INITIAL);
      db.saveDownloadMediaModel(cachedModel);
      callback.onSuccess(cachedModel);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.fail;
   }
}
