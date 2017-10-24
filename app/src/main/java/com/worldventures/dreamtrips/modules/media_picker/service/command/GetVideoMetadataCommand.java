package com.worldventures.dreamtrips.modules.media_picker.service.command;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.media_picker.model.ImmutableVideoMetadata;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoMetadata;


import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetVideoMetadataCommand extends Command<VideoMetadata> implements InjectableAction {

   @Inject Context context;
   private Uri uri;

   private Video video;

   public GetVideoMetadataCommand(Uri uri) {
      this.uri = uri;
   }

   public GetVideoMetadataCommand(Video video) {
      this.video = video;
   }

   @Override
   protected void run(CommandCallback<VideoMetadata> commandCallback) throws Throwable {
      if (video == null) {
         createFromUri(commandCallback);
      } else {
         createFromPost(commandCallback);
      }
   }

   private void createFromPost(CommandCallback<VideoMetadata> commandCallback) {ImmutableVideoMetadata.Builder builder = ImmutableVideoMetadata.builder();
      builder.aspectRatio(video.getAspectRatio());
      builder.duration(video.getDuration());
      builder.width(0);
      builder.height(0);
      commandCallback.onSuccess(builder.build());
   }

   private void createFromUri(CommandCallback<VideoMetadata> commandCallback) {
      MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
      metaRetriever.setDataSource(context, uri);

      ImmutableVideoMetadata.Builder builder = ImmutableVideoMetadata.builder();

      builder.title(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
      builder.duration(Long.parseLong(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));

      int rotation = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
      int width = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
      int height = Integer.parseInt(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
      if (rotation == 90 || rotation == 270) {
         builder.aspectRatio((double) height / width);
         builder.width(height).height(width);
      } else {
         builder.aspectRatio((double) width / height);
         builder.width(width).height(height);
      }

      commandCallback.onSuccess(builder.build());
   }
}
