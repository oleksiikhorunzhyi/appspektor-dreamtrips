package com.worldventures.dreamtrips.modules.common.command;

import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MediaCaptureCanceledCommand extends Command<MediaPickerModel.Type> {

   private MediaPickerModel.Type type;

   public MediaCaptureCanceledCommand(MediaPickerModel.Type type) {
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<MediaPickerModel.Type> commandCallback) throws Throwable {
      commandCallback.onSuccess(type);
   }
}
