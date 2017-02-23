package com.worldventures.dreamtrips.modules.version_check.service;

import com.worldventures.dreamtrips.modules.version_check.service.command.VersionCheckCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class VersionCheckInteractor {

   private ActionPipe<VersionCheckCommand> versionCheckActionPipe;

   public VersionCheckInteractor(Janet janet) {
      versionCheckActionPipe = janet.createPipe(VersionCheckCommand.class, Schedulers.io());
   }

   public ActionPipe<VersionCheckCommand> versionCheckPipe() {
      return versionCheckActionPipe;
   }
}
