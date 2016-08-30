package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.command.DeleteCachedEntityCommand;
import com.worldventures.dreamtrips.modules.common.command.DownloadCachedEntityCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class CachedEntityInteractor {

   private ActionPipe<DownloadCachedEntityCommand> downloadCachedEntityPipe;
   private ActionPipe<DeleteCachedEntityCommand> deleteCachedEntityPipe;

   public CachedEntityInteractor(Janet janet) {
      this.downloadCachedEntityPipe = janet.createPipe(DownloadCachedEntityCommand.class, Schedulers.io());
      this.deleteCachedEntityPipe = janet.createPipe(DeleteCachedEntityCommand.class, Schedulers.io());
   }

   public ActionPipe<DownloadCachedEntityCommand> getDownloadCachedEntityPipe() {
      return downloadCachedEntityPipe;
   }

   public ActionPipe<DeleteCachedEntityCommand> getDeleteCachedEntityPipe() {
      return deleteCachedEntityPipe;
   }
}
