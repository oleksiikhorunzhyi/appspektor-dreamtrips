package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoSocialized;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import io.techery.mappery.MapperyContext;

public class PhotoSocializedConverter extends PhotoConverter<PhotoSocialized> {

   @Override
   public Class<PhotoSocialized> sourceClass() {
      return PhotoSocialized.class;
   }

   @Override
   public Photo convert(MapperyContext mapperyContext, PhotoSocialized apiPhoto) {
      Photo photo = super.convert(mapperyContext, apiPhoto);

      photo.setOwner(mapperyContext.convert(apiPhoto.author(), User.class));

      photo.setLiked(apiPhoto.liked());
      photo.setLikesCount(apiPhoto.likes());

      photo.setCommentsCount(apiPhoto.commentsCount());
      photo.setComments(mapperyContext.convert(apiPhoto.comments(), Comment.class));

      return photo;
   }
}
