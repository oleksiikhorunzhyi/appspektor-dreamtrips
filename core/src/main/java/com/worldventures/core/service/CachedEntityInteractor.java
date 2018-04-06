package com.worldventures.core.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.video.service.command.ResetCachedModelsInProgressCommand;
import com.worldventures.core.modules.video.service.command.UpdateStatusCachedEntityCommand;
import com.worldventures.core.service.command.DeleteCachedModelCommand;
import com.worldventures.core.service.command.DownloadCachedModelCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CachedEntityInteractor {

   private final ActionPipe<DownloadCachedModelCommand> downloadCachedModelPipe;
   private final ActionPipe<DeleteCachedModelCommand> deleteCachedModelPipe;
   private final ActionPipe<ResetCachedModelsInProgressCommand> resetCachedModelsInProgressPipe;
   private final ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe;

   public CachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
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

   public ActionPipe<UpdateStatusCachedEntityCommand> updateStatusCachedEntityCommandPipe() {
      return updateStatusCachedEntityCommandPipe;
   }
}
