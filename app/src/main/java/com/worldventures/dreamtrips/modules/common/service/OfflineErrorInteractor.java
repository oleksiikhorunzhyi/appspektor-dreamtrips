package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand;

import io.techery.janet.ActionPipe;

public class OfflineErrorInteractor {

   private ActionPipe<OfflineErrorCommand> offlineErrorCommandPipe;

   public OfflineErrorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      offlineErrorCommandPipe = sessionActionPipeCreator.createPipe(OfflineErrorCommand.class);
   }

   public ActionPipe<OfflineErrorCommand> offlineErrorCommandPipe() {
      return offlineErrorCommandPipe;
   }
}
