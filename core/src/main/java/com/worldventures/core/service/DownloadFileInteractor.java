package com.worldventures.core.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.service.command.DownloadFileCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class DownloadFileInteractor {

   private final ActionPipe<DownloadFileCommand> downloadFileCommandPipe;

   public DownloadFileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.downloadFileCommandPipe = sessionActionPipeCreator.createPipe(DownloadFileCommand.class, Schedulers.io());
   }

   public ActionPipe<DownloadFileCommand> getDownloadFileCommandPipe() {
      return downloadFileCommandPipe;
   }
}
