package com.worldventures.dreamtrips.social.ui.feed.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostDescriptionCreatedCommand;

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
