package com.worldventures.dreamtrips.modules.media_picker.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetVideoMetadataCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class MediaMetadataInteractor {

   private ActionPipe<GetVideoMetadataCommand> videoMetadataCommandActionPipe;

   public MediaMetadataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.videoMetadataCommandActionPipe = sessionActionPipeCreator.createPipe(GetVideoMetadataCommand.class, Schedulers
            .io());
   }

   public ActionPipe<GetVideoMetadataCommand> videoMetadataCommandActionPipe() {
      return videoMetadataCommandActionPipe;
   }
}
