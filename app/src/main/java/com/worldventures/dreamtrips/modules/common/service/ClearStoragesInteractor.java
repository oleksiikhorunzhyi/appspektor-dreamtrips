package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.common.command.ClearStoragesCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class ClearStoragesInteractor {

   private final ActionPipe<ClearStoragesCommand> clearMemoryStorageActionPipe;
   private final ActionPipe<CleanTempDirectoryCommand> cleanTempDirectoryPipe;

   @Inject
   public ClearStoragesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      clearMemoryStorageActionPipe = sessionActionPipeCreator.createPipe(ClearStoragesCommand.class, Schedulers.io());
      cleanTempDirectoryPipe = sessionActionPipeCreator.createPipe(CleanTempDirectoryCommand.class, Schedulers.io());
   }

   public ActionPipe<ClearStoragesCommand> clearMemoryStorageActionPipe() {
      return clearMemoryStorageActionPipe;
   }

   public ActionPipe<CleanTempDirectoryCommand> cleanTempDirectoryPipe() {
      return cleanTempDirectoryPipe;
   }
}
