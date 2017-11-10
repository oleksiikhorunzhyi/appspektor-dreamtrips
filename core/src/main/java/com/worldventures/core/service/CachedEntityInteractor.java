package com.worldventures.core.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.video.service.command.ResetCachedModelsInProgressCommand;
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand;
import com.worldventures.core.service.command.DeleteCachedModelCommand;
import com.worldventures.core.service.command.DownloadCachedModelCommand;
import com.worldventures.core.service.command.MigrateFromCachedEntityCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CachedEntityInteractor {

   private ActionPipe<MigrateFromCachedEntityCommand> migrateFromCachedEntityPipe;
   private ActionPipe<DownloadCachedModelCommand> downloadCachedModelPipe;
   private ActionPipe<DeleteCachedModelCommand> deleteCachedModelPipe;
   private ActionPipe<ResetCachedModelsInProgressCommand> resetCachedModelsInProgressPipe;
   private ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe;

   public CachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.migrateFromCachedEntityPipe = sessionActionPipeCreator.createPipe(MigrateFromCachedEntityCommand.class, Schedulers
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

   public ActionPipe<MigrateFromCachedEntityCommand> getMigrateFromCachedEntityPipe() {
      return migrateFromCachedEntityPipe;
   }

   public ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe() {
      return updateStatusCachedEntityCommandPipe;
   }
}