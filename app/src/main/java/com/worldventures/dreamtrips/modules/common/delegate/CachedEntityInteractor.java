package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedModelCommand;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedModelsInProgressCommand;
import com.worldventures.dreamtrips.modules.common.command.UpdateStatusCachedEntityCommand;
import com.worldventures.dreamtrips.modules.video.service.command.MigrateFromCachedEntity;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CachedEntityInteractor {

   private ActionPipe<MigrateFromCachedEntity> migrateFromCachedEntityPipe;
   private ActionPipe<DownloadCachedModelCommand> downloadCachedModelPipe;
   private ActionPipe<DeleteCachedModelCommand> deleteCachedModelPipe;
   private ActionPipe<ResetCachedModelsInProgressCommand> resetCachedModelsInProgressPipe;
   private ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe;

   public CachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.migrateFromCachedEntityPipe = sessionActionPipeCreator.createPipe(MigrateFromCachedEntity.class, Schedulers
            .io());
      this.downloadCachedModelPipe = sessionActionPipeCreator.createPipe(DownloadCachedModelCommand.class, Schedulers
            .io());
      this.deleteCachedModelPipe = sessionActionPipeCreator.createPipe(DeleteCachedModelCommand.class, Schedulers.io());
      this.resetCachedModelsInProgressPipe = sessionActionPipeCreator.createPipe(ResetCachedModelsInProgressCommand.class, Schedulers
            .io());
      this.updateStatusCachedEntityCommandPipe = sessionActionPipeCreator.createPipe(UpdateStatusCachedEntityCommand.class, Schedulers
            .io());

   }

   public ActionPipe<DownloadCachedModelCommand> getDownloadCachedModelPipe() {
      return downloadCachedModelPipe;
   }

   public ActionPipe<DeleteCachedModelCommand> getDeleteCachedModelPipe() {
      return deleteCachedModelPipe;
   }

   public ActionPipe<ResetCachedModelsInProgressCommand> getResetCachedModelsInProgressPipe() {
      return resetCachedModelsInProgressPipe;
   }

   public ActionPipe<MigrateFromCachedEntity> getMigrateFromCachedEntityPipe() {
      return migrateFromCachedEntityPipe;
   }

   public ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe() {
      return updateStatusCachedEntityCommandPipe;
   }
}
