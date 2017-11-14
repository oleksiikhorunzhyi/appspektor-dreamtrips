package com.worldventures.core.modules.picker.command;


import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.picker.model.VideoPickerModel;
import com.worldventures.core.modules.picker.service.delegate.VideosProvider;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetVideosFromGalleryCommand extends Command<List<VideoPickerModel>> implements InjectableAction {

   @Inject VideosProvider videosProvider;

   private final int count;

   public GetVideosFromGalleryCommand() {
      this(Integer.MAX_VALUE);
   }

   public GetVideosFromGalleryCommand(int count) {
      this.count = count;
   }

   @Override
   protected void run(CommandCallback<List<VideoPickerModel>> callback) throws Throwable {
      callback.onSuccess(videosProvider.provide(count));
   }
}
