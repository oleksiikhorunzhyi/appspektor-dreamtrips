package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class CreatePostBodyInteractor {

   private final ActionPipe<PostDescriptionCreatedCommand> postDescriptionPipe;

   @Inject
   public CreatePostBodyInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      postDescriptionPipe = sessionActionPipeCreator.createPipe(PostDescriptionCreatedCommand.class, Schedulers.io());
   }

   public ActionPipe<PostDescriptionCreatedCommand> getPostDescriptionPipe() {
      return postDescriptionPipe;
   }
}
