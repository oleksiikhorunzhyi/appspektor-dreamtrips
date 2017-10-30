package com.worldventures.core.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.CachedEntity;
import com.worldventures.core.model.CachedModel;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.core.modules.video.model.Status.CacheStatus;
import static com.worldventures.core.modules.video.model.Status.FAILED;
import static com.worldventures.core.modules.video.model.Status.IN_PROGRESS;
import static com.worldventures.core.modules.video.model.Status.SUCCESS;

@CommandAction
public class MigrateFromCachedEntityCommand extends Command<Void> implements InjectableAction {

   @Inject MediaModelStorage storage;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      List<CachedEntity> entities = storage.getDownloadMediaEntities();

      if (entities.isEmpty()) {
         callback.onSuccess(null);
         return;
      }

      Queryable.from(entities).map(this::convert)
            .forEachR(cachedModel -> storage.saveDownloadMediaModel(cachedModel));
      storage.deleteAllMediaEntities();
   }

   private CachedModel convert(CachedEntity entity) {
      CachedModel cachedModel = new CachedModel(entity.getUrl(), entity.getUuid(), entity.getName());
      @CacheStatus int status = entity.getProgress() == 100 ? SUCCESS : entity.isFailed() ? FAILED : IN_PROGRESS;
      cachedModel.setCacheStatus(status);
      cachedModel.setProgress(entity.getProgress());
      return cachedModel;
   }

}
