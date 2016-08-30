package com.worldventures.dreamtrips.modules.common.delegate;

import com.worldventures.dreamtrips.modules.common.command.DownloadFileCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class DownloadFileInteractor {
   private ActionPipe<DownloadFileCommand> downloadFileCommandPipe;

   public DownloadFileInteractor(Janet janet) {
      this.downloadFileCommandPipe = janet.createPipe(DownloadFileCommand.class, Schedulers.io());
   }

   public ActionPipe<DownloadFileCommand> getDownloadFileCommandPipe() {
      return downloadFileCommandPipe;
   }
}
