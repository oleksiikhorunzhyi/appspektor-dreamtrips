package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.feed.model.VideoQuality;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;

import java.util.List;

import io.techery.mappery.MapperyContext;

public abstract class BaseVideoConverter<T extends com.worldventures.dreamtrips.api.multimedia.model.Video> implements Converter<T, Video> {
   @Override
   public Class<Video> targetClass() {
      return Video.class;
   }

   @Override
   public Video convert(MapperyContext mapperyContext, T videoAttachment) {
      Video video = new Video();
      if (videoAttachment.ratio() != null) video.setAspectRatio(videoAttachment.ratio());
      if (videoAttachment.qualities() != null) {
         VideoQuality hdVideoQuality = getVideoQuality("hd", videoAttachment.qualities());
         VideoQuality sdVideoQuality = getVideoQuality("sd", videoAttachment.qualities());
         if (hdVideoQuality != null) video.setHdUrl(hdVideoQuality.uri());
         if (sdVideoQuality != null) video.setSdUrl(sdVideoQuality.uri());
      }
      video.setThumbnail(videoAttachment.thumbnail());
      video.setUid(videoAttachment.uid());
      video.setUploadId(videoAttachment.uploadId());
      video.setCreatedAt(videoAttachment.createdAt());
      video.setDuration(videoAttachment.duration());
      return video;
   }

   private VideoQuality getVideoQuality(String type, List<VideoQuality> qualities) {
      return Queryable.from(qualities).firstOrDefault(quality -> type.equalsIgnoreCase(quality.quality()));
   }
}
