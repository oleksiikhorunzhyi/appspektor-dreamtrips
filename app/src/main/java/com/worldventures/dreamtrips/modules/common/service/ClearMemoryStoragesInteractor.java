package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.modules.common.api.janet.command.ClearMemoryStorageCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class ClearMemoryStoragesInteractor {

   private final ActionPipe<ClearMemoryStorageCommand> clearMemoryStorageActionPipe;

   @Inject
   public ClearMemoryStoragesInteractor(Janet janet) {
      clearMemoryStorageActionPipe = janet.createPipe(ClearMemoryStorageCommand.class, Schedulers.io());
   }

   public ActionPipe<ClearMemoryStorageCommand> clearMemoryStorageActionPipe() {
      return clearMemoryStorageActionPipe;
   }
}
