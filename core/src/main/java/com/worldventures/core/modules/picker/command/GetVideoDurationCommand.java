package com.worldventures.core.modules.picker.command;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.worldventures.janet.injection.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetVideoDurationCommand extends Command<Long> implements InjectableAction {

   @Inject Context context;

   private Uri uri;

   public GetVideoDurationCommand(Uri uri) {
      this.uri = uri;
   }

   @Override
   protected void run(CommandCallback<Long> callback) throws Throwable {
      MediaMetadataRetriever retriever = new MediaMetadataRetriever();
      retriever.setDataSource(context, uri);
      String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
      if (duration != null) {
         callback.onSuccess(Long.parseLong(duration));
      } else {
         callback.onFail(new IllegalStateException("Failed to get video duration"));
      }
   }

   public Uri getUri() {
      return uri;
   }
}
