package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter

import com.worldventures.core.converter.Converter
import com.worldventures.dreamtrips.social.ui.feed.model.video.Quality
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import io.techery.mappery.MapperyContext

typealias ApiVideo = com.worldventures.dreamtrips.api.multimedia.model.Video

abstract class BaseVideoConverter<T : ApiVideo> : Converter<T, Video> {
   override fun targetClass(): Class<Video> = Video::class.java

   override fun convert(mapperyContext: MapperyContext, videoAttachment: T): Video {
      val video = Video()
      var videoAttachmentRatio = videoAttachment.ratio()
      if (videoAttachmentRatio != null) {
         video.aspectRatio = videoAttachmentRatio
      }
      val videoQualities = videoAttachment.qualities()
      if (videoQualities != null) {
         video.qualities = videoQualities.map { it -> Quality(it.quality(), it.uri()) }.toList()
      }
      video.thumbnail = videoAttachment.thumbnail()
      video.uid = videoAttachment.uid()
      video.uploadId = videoAttachment.uploadId()
      video.createdAt = videoAttachment.createdAt()
      video.duration = videoAttachment.duration()
      return video
   }
}
