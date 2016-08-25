package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class CreatePostBodyInteractor {

   private final ActionPipe<PostDescriptionCreatedCommand> postDescriptionPipe;

   @Inject
   public CreatePostBodyInteractor(Janet janet) {
      postDescriptionPipe = janet.createPipe(PostDescriptionCreatedCommand.class, Schedulers.io());
   }

   public ActionPipe<PostDescriptionCreatedCommand> getPostDescriptionPipe() {
      return postDescriptionPipe;
   }
}
