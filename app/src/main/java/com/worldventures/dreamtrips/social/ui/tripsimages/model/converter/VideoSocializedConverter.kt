package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter

import com.worldventures.core.model.User
import com.worldventures.dreamtrips.api.multimedia.model.VideoSocialized
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import io.techery.mappery.MapperyContext

typealias ApiComment = com.worldventures.dreamtrips.api.comment.model.Comment

class VideoSocializedConverter : BaseVideoConverter<VideoSocialized>() {

   override fun sourceClass(): Class<VideoSocialized> {
      return VideoSocialized::class.java
   }

   override fun convert(mapperyContext: MapperyContext, apiVideo: VideoSocialized): Video {
      val video = super.convert(mapperyContext, apiVideo)
      video.owner = mapperyContext.convert(apiVideo.author(), User::class.java)
      video.isLiked = apiVideo.liked()
      video.likesCount = apiVideo.likes()
      video.commentsCount = apiVideo.commentsCount()

      val commentsList = apiVideo.comments()
      val comments: List<ApiComment> = commentsList ?: listOf<ApiComment>()
      video.comments = mapperyContext.convert(comments, Comment::class.java)

      return video
   }
}
