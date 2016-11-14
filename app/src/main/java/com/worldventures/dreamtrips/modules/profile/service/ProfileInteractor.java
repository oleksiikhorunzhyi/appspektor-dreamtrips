package com.worldventures.dreamtrips.modules.profile.service;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPrivateProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadAvatarCommand;
import com.worldventures.dreamtrips.modules.profile.service.command.UploadBackgroundCommand;

import io.techery.janet.ActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class ProfileInteractor {

   private ActionPipe<GetPrivateProfileCommand> privateProfilePipe;
   private ActionPipe<UploadAvatarCommand> uploadAvatarPipe;
   private ActionPipe<UploadBackgroundCommand> uploadBackgroundPipe;
   private ActionPipe<GetPublicProfileCommand> publicProfilePipe;

   private SessionHolder<UserSession> sessionHolder;

   public ProfileInteractor(SessionActionPipeCreator sessionActionPipeCreator, SessionHolder<UserSession> sessionHolder) {
      privateProfilePipe = sessionActionPipeCreator.createPipe(GetPrivateProfileCommand.class, Schedulers.io());
      publicProfilePipe = sessionActionPipeCreator.createPipe(GetPublicProfileCommand.class, Schedulers.io());
      uploadAvatarPipe = sessionActionPipeCreator.createPipe(UploadAvatarCommand.class, Schedulers.io());
      uploadBackgroundPipe = sessionActionPipeCreator.createPipe(UploadBackgroundCommand.class, Schedulers.io());
      this.sessionHolder = sessionHolder;
      listenToPrivateProfileUpdates();
   }

   public ActionPipe<GetPrivateProfileCommand> privateProfilePipe() {
      return privateProfilePipe;
   }

   public ActionPipe<GetPublicProfileCommand> publicProfilePipe() {
      return publicProfilePipe;
   }

   public ActionPipe<UploadBackgroundCommand> uploadBackgroundPipe() {
      return uploadBackgroundPipe;
   }

   public ActionPipe<UploadAvatarCommand> uploadAvatarPipe() {
      return uploadAvatarPipe;
   }

   private void listenToPrivateProfileUpdates() {
      Observable.merge(privateProfilePipe.observeSuccess(),
            uploadAvatarPipe.observeSuccess(),
            uploadBackgroundPipe.observeSuccess())
            .subscribe(command -> {
               UserSession userSession = sessionHolder.get().get();
               userSession.setUser(command.getResult());
               sessionHolder.put(userSession);
            });

   }
}
