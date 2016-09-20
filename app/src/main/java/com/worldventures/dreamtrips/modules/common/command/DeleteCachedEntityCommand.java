package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteCachedEntityCommand extends CachedEntityCommand implements InjectableAction {

   private File file;

   @Inject SnappyRepository db;

   public DeleteCachedEntityCommand(CachedEntity cachedEntity, File file) {
      super(cachedEntity);
      this.file = file;
   }

   @Override
   protected void run(CommandCallback<CachedEntity> callback) throws Throwable {
      file.delete();
      cachedEntity.setProgress(0);
      db.saveDownloadMediaEntity(cachedEntity);
      callback.onSuccess(cachedEntity);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.fail;
   }
}
