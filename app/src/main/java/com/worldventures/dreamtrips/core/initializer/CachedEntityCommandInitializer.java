package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.util.List;

import javax.inject.Inject;

public class CachedEntityCommandInitializer implements AppInitializer {

   @Inject SnappyRepository snappyDB;

   public CachedEntityCommandInitializer(SnappyRepository snappyDB) {
      this.snappyDB = snappyDB;
   }

   @Override
   public void initialize(Injector injector) {
      // If app is starting and there are some pending downloads, it means they are failed.
      // Clear them up.
      List<CachedEntity> entities = snappyDB.getDownloadMediaEntities();
      for (CachedEntity entity : entities) {
         if (entity.inProgress()) {
            entity.setIsFailed(true);
            entity.setProgress(0);
         }
         snappyDB.saveDownloadMediaEntity(entity);
      }
   }
}
