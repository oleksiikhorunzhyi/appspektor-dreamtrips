package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;

@Singleton
public class SuggestedPhotoInteractor {

   private final ActionPipe<SuggestedPhotoCommand> suggestedPhotoCommandActionPipe;

   @Inject
   public SuggestedPhotoInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      suggestedPhotoCommandActionPipe = sessionActionPipeCreator.createPipe(SuggestedPhotoCommand.class);
   }

   public ActionPipe<SuggestedPhotoCommand> getSuggestedPhotoCommandActionPipe() {
      return suggestedPhotoCommandActionPipe;
   }
}
