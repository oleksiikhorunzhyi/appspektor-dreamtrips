package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Quality;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;

import io.techery.mappery.MapperyContext;

public abstract class BaseVideoConverter<T extends com.worldventures.dreamtrips.api.multimedia.model.Video> implements Converter<T, Video> {
   @Override
   public Class<Video> targetClass() {
      return Video.class;
   }

   @Override
   public Video convert(MapperyContext mapperyContext, T videoAttachment) {
      Video video = new Video();
      if (videoAttachment.ratio() != null) {
         video.setAspectRatio(videoAttachment.ratio());
      }
      if (videoAttachment.qualities() != null) {
         video.setQualities(Queryable.from(videoAttachment.qualities())
               .map(it -> new Quality(it.quality(), it.uri())).toList());
      }
      video.setThumbnail(videoAttachment.thumbnail());
      video.setUid(videoAttachment.uid());
      video.setUploadId(videoAttachment.uploadId());
      video.setCreatedAt(videoAttachment.createdAt());
      video.setDuration(videoAttachment.duration());
      return video;
   }
}
