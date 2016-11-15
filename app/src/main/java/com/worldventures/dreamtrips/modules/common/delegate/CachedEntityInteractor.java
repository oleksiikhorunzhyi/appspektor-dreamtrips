package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.DeleteCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.ResetCachedEntitiesInProgressCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class CachedEntityInteractor {

   private ActionPipe<DownloadCachedEntityCommand> downloadCachedEntityPipe;
   private ActionPipe<DeleteCachedEntityCommand> deleteCachedEntityPipe;
   private ActionPipe<ResetCachedEntitiesInProgressCommand> resetCachedEntitiesInProgressPipe;

   public CachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.downloadCachedEntityPipe = sessionActionPipeCreator.createPipe(DownloadCachedEntityCommand.class, Schedulers.io());
      this.deleteCachedEntityPipe = sessionActionPipeCreator.createPipe(DeleteCachedEntityCommand.class, Schedulers.io());
      this.resetCachedEntitiesInProgressPipe = sessionActionPipeCreator.createPipe(ResetCachedEntitiesInProgressCommand.class, Schedulers.io());
   }

   public ActionPipe<DownloadCachedEntityCommand> getDownloadCachedEntityPipe() {
      return downloadCachedEntityPipe;
   }

   public ActionPipe<DeleteCachedEntityCommand> getDeleteCachedEntityPipe() {
      return deleteCachedEntityPipe;
   }

   public ActionPipe<ResetCachedEntitiesInProgressCommand> getResetCachedEntitiesInProgressPipe() {
      return resetCachedEntitiesInProgressPipe;
   }
}