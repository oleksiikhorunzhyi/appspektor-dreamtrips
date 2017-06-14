package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;

import io.techery.janet.ActionPipe;

public class FeedItemsVideoProcessingStatusInteractor {

   private ActionPipe<FeedItemsVideoProcessingStatusCommand> videosProcessingPipe;

   public FeedItemsVideoProcessingStatusInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      videosProcessingPipe = sessionActionPipeCreator.createPipe(FeedItemsVideoProcessingStatusCommand.class);
   }

   public ActionPipe<FeedItemsVideoProcessingStatusCommand> videosProcessingPipe() {
      return videosProcessingPipe;
   }
}
