package com.worldventures.dreamtrips.modules.profile.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.profile.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.command.GetPublicProfileCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class ProfileInteractor {

   private ActionPipe<GetPrivateProfileCommand> privateProfilePipe;
   private ActionPipe<GetPublicProfileCommand> publicProfilePipe;

   public ProfileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      privateProfilePipe = sessionActionPipeCreator.createPipe(GetPrivateProfileCommand.class, Schedulers.io());
      publicProfilePipe = sessionActionPipeCreator.createPipe(GetPublicProfileCommand.class, Schedulers.io());
   }

   public ActionPipe<GetPrivateProfileCommand> privateProfilePipe() {
      return privateProfilePipe;
   }

   public ActionPipe<GetPublicProfileCommand> publicProfilePipe() {
      return publicProfilePipe;
   }
}
