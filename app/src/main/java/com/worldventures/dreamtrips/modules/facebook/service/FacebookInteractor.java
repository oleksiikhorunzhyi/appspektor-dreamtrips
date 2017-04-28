package com.worldventures.dreamtrips.modules.facebook.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class FacebookInteractor {

   private final ActionPipe<GetAlbumsCommand> albumsPipe;
   private final ActionPipe<GetPhotosCommand> photosPipe;

   @Inject
   public FacebookInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.albumsPipe = sessionActionPipeCreator.createPipe(GetAlbumsCommand.class, Schedulers.io());
      this.photosPipe = sessionActionPipeCreator.createPipe(GetPhotosCommand.class, Schedulers.io());
   }

   public ActionPipe<GetAlbumsCommand> albumsPipe() {
      return albumsPipe;
   }

   public ActionPipe<GetPhotosCommand> photosPipe() {
      return photosPipe;
   }
}
