package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

@Singleton
public class SmartCardAvatarInteractor {

   private final ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe;

   @Inject public SmartCardAvatarInteractor(Janet janet) {
      smartCardAvatarPipe = janet.createPipe(SmartCardAvatarCommand.class, Schedulers.io());
   }

   public ActionPipe<SmartCardAvatarCommand> smartCardAvatarPipe() {
      return smartCardAvatarPipe;
   }
}
