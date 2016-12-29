package com.worldventures.dreamtrips.modules.feed.converter;

import com.worldventures.dreamtrips.api.photos.model.PhotoWithAuthor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import io.techery.mappery.MapperyContext;

public class PhotoWithAuthorConverter extends PhotoConverter<PhotoWithAuthor> {

   @Override
   public Class<PhotoWithAuthor> sourceClass() {
      return PhotoWithAuthor.class;
   }

   @Override
   public Photo convert(MapperyContext mapperyContext, PhotoWithAuthor apiPhoto) {
      Photo photo = super.convert(mapperyContext, apiPhoto);

      photo.setOwner(mapperyContext.convert(apiPhoto.author(), User.class));
      return photo;
   }
}
