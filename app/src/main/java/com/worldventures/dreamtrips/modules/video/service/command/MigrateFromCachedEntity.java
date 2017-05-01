package com.worldventures.dreamtrips.modules.video.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.CachedModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.modules.video.model.Status.*;

@CommandAction
public class MigrateFromCachedEntity extends Command<Void> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      List<CachedEntity> entities = snappyRepository.getDownloadMediaEntities();

      if (entities.isEmpty()) {
         callback.onSuccess(null);
         return;
      }

      Queryable.from(entities).map(this::convert)
            .forEachR(cachedModel -> snappyRepository.saveDownloadMediaModel(cachedModel));
      snappyRepository.deleteAllMediaEntities();
   }

   private CachedModel convert(CachedEntity entity) {
      CachedModel cachedModel = new CachedModel(entity.getUrl(), entity.getUuid(), entity.getName());
      @CacheStatus int status = entity.getProgress() == 100? SUCCESS : entity.isFailed()? FAILED : IN_PROGRESS;
      cachedModel.setCacheStatus(status);
      cachedModel.setProgress(entity.getProgress());
      return cachedModel;
   }

}
