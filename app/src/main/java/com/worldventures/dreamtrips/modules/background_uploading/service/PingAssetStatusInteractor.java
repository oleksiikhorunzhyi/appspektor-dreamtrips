package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.LaunchUpdatingVideoProcessingCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.PerformUpdateVideoStatusCommand;
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.UpdateVideoProcessStatusCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class PingAssetStatusInteractor {

   private ActionPipe<UpdateVideoProcessStatusCommand> updateVideoProcessStatusPipe;
   private ActionPipe<PerformUpdateVideoStatusCommand> performUpdateVideoStatusPipe;
   private ActionPipe<LaunchUpdatingVideoProcessingCommand> launchUpdatingVideoProcessingPipe;
   private ActionPipe<FeedItemsVideoProcessingStatusCommand> feedItemsVideoProcessingPipe;

   public PingAssetStatusInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      updateVideoProcessStatusPipe = sessionActionPipeCreator.createPipe(UpdateVideoProcessStatusCommand.class, Schedulers.io());
      performUpdateVideoStatusPipe = sessionActionPipeCreator.createPipe(PerformUpdateVideoStatusCommand.class, Schedulers.io());
      launchUpdatingVideoProcessingPipe = sessionActionPipeCreator.createPipe(LaunchUpdatingVideoProcessingCommand.class, Schedulers.io());
      feedItemsVideoProcessingPipe = sessionActionPipeCreator.createPipe(FeedItemsVideoProcessingStatusCommand.class, Schedulers.io());
   }

   public ActionPipe<UpdateVideoProcessStatusCommand> updateVideoProcessStatusPipe() {
      return updateVideoProcessStatusPipe;
   }

   public ActionPipe<PerformUpdateVideoStatusCommand> performUpdateVideoStatusPipe() {
      return performUpdateVideoStatusPipe;
   }

   public ActionPipe<LaunchUpdatingVideoProcessingCommand> launchUpdatingVideoProcessingPipe() {
      return launchUpdatingVideoProcessingPipe;
   }

   public ActionPipe<FeedItemsVideoProcessingStatusCommand> feedItemsVideoProcessingPipe() {
      return feedItemsVideoProcessingPipe;
   }
}
