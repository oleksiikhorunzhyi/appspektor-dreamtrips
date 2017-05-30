package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCachedModelCommand extends CachedEntityCommand implements InjectableAction {

   private File file;

   @Inject SnappyRepository db;

   public DeleteCachedModelCommand(CachedModel cachedModel, File file) {
      super(cachedModel);
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<CachedModel> callback) throws Throwable {
      file.delete();
      cachedModel.setProgress(0);
      db.saveDownloadMediaModel(cachedModel);
      callback.onSuccess(cachedModel);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.fail;
   }
}
