package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter;

import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.api.multimedia.model.VideoSocialized;
import com.worldventures.dreamtrips.social.ui.feed.model.comment.Comment;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;

import java.util.ArrayList;

import io.techery.mappery.MapperyContext;

public class VideoSocializedConverter extends BaseVideoConverter<VideoSocialized> {

   @Override
   public Class<VideoSocialized> sourceClass() {
      return VideoSocialized.class;
   }

   @Override
   public Video convert(MapperyContext mapperyContext, VideoSocialized apiVideo) {
      Video video = super.convert(mapperyContext, apiVideo);
      video.setOwner(mapperyContext.convert(apiVideo.author(), User.class));
      video.setLiked(apiVideo.liked());
      video.setLikesCount(apiVideo.likes());
      video.setCommentsCount(apiVideo.commentsCount());

      if (apiVideo.comments() != null) {
         video.setComments(mapperyContext.convert(apiVideo.comments(), Comment.class));
      } else {
         video.setComments(new ArrayList<>());
      }
      return video;
   }
}
