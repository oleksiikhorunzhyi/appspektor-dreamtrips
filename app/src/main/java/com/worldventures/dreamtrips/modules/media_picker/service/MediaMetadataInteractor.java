package com.worldventures.dreamtrips.modules.media_picker.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetVideoMetadataCommand;
import com.worldventures.dreamtrips.modules.media_picker.service.command.RecognizeFacesCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class MediaMetadataInteractor {

   private final ActionPipe<GetVideoMetadataCommand> videoMetadataCommandActionPipe;
   private final ActionPipe<RecognizeFacesCommand> recognizeFacesCommandActionPipe;

   public MediaMetadataInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      this.videoMetadataCommandActionPipe = sessionActionPipeCreator.createPipe(GetVideoMetadataCommand.class, Schedulers
            .io());
      this.recognizeFacesCommandActionPipe = sessionActionPipeCreator.createPipe(RecognizeFacesCommand.class, Schedulers
            .io());
   }

   public ActionPipe<GetVideoMetadataCommand> videoMetadataCommandActionPipe() {
      return videoMetadataCommandActionPipe;
   }

   public ActionPipe<RecognizeFacesCommand> recognizeFacesCommandActionPipe() {
      return recognizeFacesCommandActionPipe;
   }
}
