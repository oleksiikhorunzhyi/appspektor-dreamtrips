package com.worldventures.dreamtrips.modules.feed.converter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.feed.model.VideoAttachment;
import com.worldventures.dreamtrips.api.feed.model.VideoQuality;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import java.util.List;

import io.techery.mappery.MapperyContext;

public class VideoAttachmentConverter implements Converter<VideoAttachment, Video> {

   @Override
   public Class<VideoAttachment> sourceClass() {
      return VideoAttachment.class;
   }

   @Override
   public Class<Video> targetClass() {
      return Video.class;
   }

   @Override
   public Video convert(MapperyContext mapperyContext, VideoAttachment videoAttachment) {
      Video video = new Video();
      video.setAspectRatio(videoAttachment.ratio());
      VideoQuality hdVideoQuality = getVideoQuality("hd", videoAttachment.qualities());
      VideoQuality sdVideoQuality = getVideoQuality("sd", videoAttachment.qualities());
      if (hdVideoQuality != null) video.setHdUrl(hdVideoQuality.uri());
      if (sdVideoQuality != null) video.setSdUrl(sdVideoQuality.uri());
      video.setThumbnail(videoAttachment.thumbnail());
      video.setUid(videoAttachment.uid());
      video.setUploadId(videoAttachment.uploadId());
      return video;
   }

   private VideoQuality getVideoQuality(String type, List<VideoQuality> qualities) {
      return Queryable.from(qualities)
            .firstOrDefault(quality -> type.equalsIgnoreCase(quality.quality()));
   }
}
